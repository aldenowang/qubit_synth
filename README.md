# Overview
The goal of this project is to efficiently synthesize an arbitrary quantum gate exactly, using only the gates within the Clifford + T gate set.

All operations on a quantum computer rely on rotating qubits in very specific ways. In theory, a qubit can be rotated in infinitely many ways, almost like rotating an arrow by any angle in 3D space. Mathematically, these rotations are described by *unitary matrices* with entries in complex rings. However, real quantum computers cannot perform arbitrary exact rotations directly. Instead, they are restricted to a fixed “alphabet” of basic gates that are physically implementable and robust.

The Clifford + T gate set is universal. Much like how 26 letters can form every word in the English language, the gates in the Clifford + T set (H and T) can express every single-qubit gate with entries in the ring $D[\omega]$. The challenge is not *if* a gate can be synthesized, but *how*. This project implements a version of the KMM Single Qubit Exact Synthesis algorithm [1]. The input is a unitary matrix with an even smallest denominator exponent and entries in $D[\omega]$. The output is an optimal H–T sequence that performs the exact same operation as the desired unitary gate.

# Mathematical Context

### Qubit

A qubit (quantum bit) is a quantum state that has some probability of measuring a $0$ or a $1$.

The $\ket{0}$ state is when there is a $100\%$ chance of measuring a $0$.

We represent a qubit as a column vector, where the top entry represents the probability of measuring $0$ and the bottom entry represents the probability of measuring $1$:

$$
\begin{pmatrix}
\alpha \\
\beta
\end{pmatrix},
\qquad \text{where } |\alpha|^2 + |\beta|^2 = 1.
$$

Quantum gates are applied to qubits by left multiplication. These gates change the probability.

---

### Unitary Matrices

Quantum gates are represented as left-multiplied unitary matrices.  
The gate must be unitary so that the total probability is preserved

We use $2 \times 2$ matrices for single-qubit synthesis because they act on a $2 \times 1$ column vector representing a single qubit.

A matrix is unitary if multiplying the matrix by its dagger gives the identity matrix.

The dagger of a matrix is its conjugate transpose.

More precisely, a matrix $U$ is unitary if

$$
U U^\dagger = I.
$$

The $2 \times 2$ identity matrix is

$$
I =
\begin{pmatrix}
1 & 0 \\
0 & 1
\end{pmatrix}.
$$

### Rings

We define the ring

$$
\mathbb{Z}[\omega] = \{\ a + b\omega + c\omega^2 + d\omega^3 \mid a,b,c,d \in \mathbb{Z} \,\}.
$$

where

$$
\omega = e^{i\pi/4}.
$$

Similarly, we define

$$
\mathbb{D}[\omega] = \{\ a + b\omega + c\omega^2 + d\omega^3 \mid a,b,c,d \in \mathbb{D} \,\},
$$

where $\mathbb{D}$ denotes dyadic rationals of the form

$$
\frac{P}{2^k}, \qquad P \in \mathbb{Z}, \quad k \in \mathbb{Z}_{\ge 0}.
$$

Every element of $\mathbb{Z}[\omega]$ is also an element of $\mathbb{D}[\omega]$, since

$$
\mathbb{Z} \subseteq \mathbb{D}.
$$

### Gates of the Clifford + T Set

###### Hadamard Gate ($H$)

The Hadamard gate is defined as

$$
H =
\frac{1}{\sqrt{2}}
\begin{pmatrix}
1 & 1 \\
1 & -1
\end{pmatrix}.
$$

Multiplication by $\frac{1}{\sqrt{2}}$ increases the smallest denominator exponent (SDE) by $2$, since

$$
\sqrt{2} \ u = \delta^2
$$

in the standard $\delta$-notation used for exact synthesis.

---

###### T Gate

The $T$ gate is defined as

$$
T =
\begin{pmatrix}
1 & 0 \\
0 & \omega
\end{pmatrix},
$$

where

$$
\omega = e^{i\pi/4}.
$$

### Variables \& Identities

We define

$$
\omega = e^{i\pi/4}.
$$

For integers $k \in \mathbb{Z}$,

$$
\omega^k = \left(e^{i\pi/4}\right)^k = e^{ik\pi/4}.
$$

Since $\omega^8 = 1$, powers of $\omega$ are periodic modulo $8$.  
Thus any $\omega^k$ can be written in terms of powers
$\omega^0, \omega^1, \omega^2, \omega^3$.

Using Euler's formula,

$$
\omega^k = \cos\left(\frac{k\pi}{4}\right) + i\sin\left(\frac{k\pi}{4}\right).
$$

We define

$$
\delta = 1 + \omega.
$$

Important identities:

$$
\delta^2 = \sqrt{2}\ u,
$$

$$
\delta^4 = 2\ u',
$$

where $u, u'$ are units in the ring $\mathbb{Z}[\omega]$ (and therefore also units in $\mathbb{D}[\omega]$, since $\mathbb{Z}[\omega] \subset \mathbb{D}[\omega]$)

Units are elements with multiplicative inverses and magnitude $1$.  
Since multiplication by a unit only changes the global phase, we can discard them.

---

### Phases

###### Global Phase

A global phase has the form

$$
e^{i\rho}\left( x\ket{0} + y\ket{1} \right).
$$

Multiplying a quantum state by $e^{i\rho}$ does not affect measurement probabilities, since probabilities depend on squared magnitudes.

Units in $\mathbb{Z}[\omega]$ have magnitude $1$, so multiplying by a unit applies a global phase.  
Therefore, elements that differ by a unit are treated as equivalent for synthesis purposes.

---

###### Relative Phase

A relative phase has the form

$$
x\ket{0} + e^{i\rho} y\ket{1}.
$$

Here, only one component is multiplied by a phase factor.

Relative phase does not change measurement probabilities, but it changes the quantum state geometrically.  
On the Bloch sphere, this corresponds to a rotation about the $Z$-axis.



# High Level Algorithm Summary

Start with your desired unitary gate $U$, a $2 \times 2$ matrix with all entries in the $\mathbb{D}[\omega]$ ring.

1. Multiply the matrix by $\delta$ $(1 + \omega)$ until all entries are in the ring $\mathbb{Z}[\omega]$. Represent the number of times you multiplied by delta as the variable $k$. To keep the unitary matrix equal to the original, you will represent it as

$$
\frac{1}{\delta^k} U.
$$

2. Express a column vector $\ket{\psi}$ as the first column of $U$ in the form

$$
\ket{\psi} = \frac{1}{\delta^k}
\left( x\ket{0} + y\ket{1} \right)
$$

​	and $k$ is what we refer to as the "SDE'' (smallest denominator exponent). Since unitary matrices preserve orthogonality the same gate sequence that works on the first column will work on the whole matrix, so it is simpler to merely look at the first column.

3. For every step there is guaranteed to exist a $T^LH$ sequence where $L \in \{0,1,2,3\}$ that can reduce the SDE by 1 (refer **additional info**).

    This means cancelling out one delta in

$$
\frac{1}{\delta^k}
\left( x\ket{0} + y\ket{1} \right)
$$

​	turning it into

$$
\frac{1}{\delta^{k-1}}
\left( x'\ket{0} + y'\ket{1} \right)
$$

reducing the SDE by $1$. Keep in mind that a Hadamard gate raises the SDE by $2$ by introducing a $\sqrt{2}$ into the denominator which is equal to $\delta^2$. We do an SDE reduction operation after every Hadamard gate to factor out deltas and create a net SDE reduction.

4. Apply the $T^LH$ sequence to $\ket{\psi}$ and reduce the column after the $H$ gate. To accurately reduce up to irrelevant global phase, utilize the equalities

$$
\sqrt{2}\ u = \delta^2
\qquad \text{and} \qquad
2\ u' = \delta^4.
$$

5. Repeat steps 3 and 4 until the SDE $= 0$. This means all the entries of $\ket{\psi}$ are in the ring $\mathbb{Z}[\omega]$. We are pretty much done once the gate is in the ring $\mathbb{Z}[\omega]$ because lemma 10.1.2 in *Picturing Quantum Software* states that it must be a computational basis vector up to a global phase [2].

6. *If* $\ket{\psi}$ is in the $\ket{1}$ state as in it is in the form

$$
\ket{\psi} =
(0\ket{0} + \omega^j \ket{1}),
$$

​	then apply an $X$ gate $(HT^4H)$ to send $\ket{\psi}$ into the $0$ state which can be written as

$$
\ket{\psi} =
(\omega^j \ket{0} + 0\ket{1}).
$$

7. Apply the same sequence of gates to send the second column to the one state.

8. Reverse and dagger the gate sequence in order to go from $\lvert 0 \rangle$ to $U\lvert 0 \rangle$ (**additional info**).


9. Return the reverse daggered gate sequence

# Implementation in Java

### 1. Input

The user inputs the first column of a unitary matrix within the ring $\mathbb{D}[\omega]$ in the runner class by entering $4$ numerators and $4$ denominators for both column elements in the *Runner* class. 

**Note: The SDE MUST be even for this implementation**

Each entry is read and checked by the *readDOmega()* method that uses the *TextIO* library input system and ensures each inputted number is in the ring.

Each entry is followed by an $\omega$ of some power from $0$ - $3$ in order. The user enters $a, e$, then $b, f$, then $c, g$, then $d, h$. Matrix elements in the ring $\mathbb{D}[\omega]$ follow the form

​							$$ \frac{a}{e} + \frac{b}{f}\omega + \frac{c}{g}\omega^2 + \frac{d}{h}\omega^3 $$

where

$$
a,b,c,d \in \mathbb{Z},
$$

$$
e,f,g,h \in 2^k,
$$

### 2. Processing Input

The inputted column vector is then used as the input for the constructor in the *U2Matrix* class. The constructor assembles the whole 2x2 unitary matrix using the column vector as the first column of the matrix. Then using the *isUnitary()* method in U2Matrix to check unitarity, if this check fails the algorithm cannot proceed. 

### 3. SDE Calculation

The *convertToZOmega()* method in the runner class is used to find the SDE. To calculate the SDE in terms of $\delta$ the method multiplies the column by

$$
\delta = 1 + \omega
$$

until the entries are in the ring $${Z}[\omega] $$, checked with the *isInRing()* method in the *ZOmega* class.

To maintain equality after the multiplication we can represent the column vector as

$$
\frac{1}{\delta^k}
\left( x\ket{0} + y\ket{1} \right)
$$

where $k$ is the number of delta multiplications called the SDE.

Next we create a *cVector* (column vector) object using the *cVector* class constructor, inputting ZOmega elements $x$ and $y$ and the SDE $k$ into the constructor.

### 4. SDE Reduction

We attempt to reduce the SDE to 0 using H and T gates.

A $T$ gate acts as

$$
T =
\begin{pmatrix}
1 & 0 \\
0 & \omega
\end{pmatrix}.
$$

so it only multiplies the bottom component by a unit phase and does not affect SDE.

---

Hadamard Gate

A Hadamard gate acts as

$$
H =
\frac{1}{\sqrt{2}}
\begin{pmatrix}
1 & 1 \\
1 & -1
\end{pmatrix}.
$$

Applying $H$ to a column vector gives

$$
\frac{1}{\delta^k}
\begin{pmatrix}
x \\
y
\end{pmatrix}
\;\longmapsto\;
\frac{1}{\delta^{k+2}}
\begin{pmatrix}
x + y \\
x - y
\end{pmatrix}.
$$

Each Hadamard increases SDE by $2$ while mixing the numerator components through the sums $x+y$ and $x-y$.

After every Hadamard, the algorithm immediately attempts to reduce the state and SDE using the *factorOutAllDeltas()* method. This method operates on $\mathbb{Z}[\omega]$ numerators and proceeds in two stages.

First, if all coefficients are even, it factors out a $\delta^4 = 2$ up to a unit, reducing the SDE by $4$.

Second, it checks whether the remaining coefficients satisfy specific *parity patterns modulo 2* (refer **additional info**) that indicate divisibility by $\delta^2 = \sqrt{2}$ up to a unit. When this condition holds, division by $\delta^2 = \sqrt{2}$ is performed via a *fixed linear transformation* (refer **additional info**) on the coefficients, reducing the SDE by an additional $2$.

With this method of reduction, a single Hadamard at best can only cancel the factor it introduces, so it cannot reduce SDE on its own. For this reason, the main reduction loop applies gates in the form

$$
T^i H T^j H,
\qquad i,j \in \{0,1,2,3\},
$$

implemented in *reduceColumnVector()* method by brute-forcing over the $16$ possibilities. There is a slightly more efficient way to do this using residues, but since there are only $16$ possibilities it remains $O(1)$ either way.

After the second Hadamard, the cyclotomic elements have been sufficiently changed by repeated $x \pm y$ combinations and phase adjustments that, for some choice of $i$ and $j$, we can factor out both $\delta^2$ and $\delta^4$, leading to a net SDE decrease of $2$. This factorization is always possible by the lemma 10.1.3 *Picturing Quantum Software* which states that every $T^i H$ sequence can reduce the SDE by exactly $1$ [2].

Because SDE is always even in this convention and strictly decreases each iteration, the reduction process terminates when SDE reaches zero. Without an SDE-contributing denominator, the column lies in the ring $\mathbb{Z}[\omega]$.

The first column should be in the zero state. If the remaining $\mathbb{Z}[\omega]$ cyclotomic element is in the one state (bottom entry), we apply a final $HT^4H$ (an $X$ gate) to send it to the zero state. At this point, the state must be a computational basis vector up to a unit phase, in the form $\omega^j$ instead of $1$, completing the reduction.

Because of the unitary’s orthogonality, the same gate sequence will send the second column to the reduced $\mathbb{Z}[\omega]$ form for the one state.

This algorithm constructs a gate sequence that sends the unitary to the identity zero-state matrix. We want to output a gate sequence that goes in the opposite direction, from the zero-state identity matrix to the desired unitary. 

Finally, we reverse and dagger the gate sequence and print the output.

# Additional Info

### Why we reverse and dagger gate sequence

  The constructed sequence is designed to reduce the target state to the zero state. Suppose the algorithm produces a gate sequence

$$
V_1 V_2 \cdots V_n
$$

such that

$$
(V_1 V_2 \cdots V_n)\ U = I.
$$

This implies that

$$
V_1 V_2 \cdots V_n = U^\dagger.
$$

To recover $U$, we take the dagger of the entire sequence and taking the dagger of a product of matrices reverses the order too:

$$
(U^\dagger)^\dagger = U.
$$

Therefore,

$$
U = (V_1 V_2 \cdots V_n)^\dagger = V_n^\dagger \cdots V_2^\dagger V_1^\dagger.
$$

Thus, the reverse daggered sequence prepares the desired unitary from the identity.

### Parity Patterns Modulo 2

To factor out a $\delta^2$ we must divide by $\sqrt{2}$. The following parity patterns for cyclotomic 8th-root polynomials apply:

$$
(\text{even},\ \text{odd},\ \text{even},\ \text{odd})^2
\;\rightarrow\
(\text{even},\ \text{even},\ \text{even},\ \text{even})
$$

$$
(\text{odd},\ \text{even},\ \text{odd},\ \text{even})^2
\;\rightarrow\
(\text{even},\ \text{even},\ \text{even},\ \text{even})
$$

$$
(\text{odd},\ \text{odd},\ \text{odd},\ \text{odd})^2
\;\rightarrow\
(\text{even},\ \text{even},\ \text{even},\ \text{even})
$$

So after squaring the cyclotomic element, the cyclotomic element is divisible by $2$.  
This means that the cyclotomic element before being squared was divisible by
$$
\sqrt{2} = \delta^2.
$$

To actually divide by $\sqrt{2}$ we use a fixed linear transformation.

---

### Fixed Linear Transformation

We want to divide by

$$
\delta^2 = (1+\omega)^2 = 1 + 2\omega + \omega^2.
$$

Let

$$
z' = x + y\omega + u\omega^2 + v\omega^3,
$$

and

$$
z = (1 + 2\omega + \omega^2)
(x + y\omega + u\omega^2 + v\omega^3).
$$

To divide by $\delta^2$ we want to solve for $z'$.

The relationship between coefficients is

$$
\begin{aligned}
a &= y - v, \\
b &= x + u, \\
c &= y + v, \\
d &= u - x,
\end{aligned}
$$

where $a,b,c,d$ are the original coefficients of $z$.

Solving for the coefficients of $z'$ gives the new coefficients after division by $\delta^2$ 

$$
\begin{aligned}
x &= \frac{b - d}{2}, \\
y &= \frac{c + a}{2}, \\
u &= \frac{b + d}{2}, \\
v &= \frac{c - a}{2}.
\end{aligned}
$$

---

### Supporting Ring Algebra Classes

##### DOmega

Represents elements of $\mathbb{D}[\omega]$ using exact rational arithmetic.

Handles:

- Addition
- Multiplication
- Conjugation
- Equality checking

Ensures denominators remain powers of $2$.

---

##### ZOmega

Represents elements of $\mathbb{Z}[\omega]$.

Handles:

- Polynomial arithmetic modulo $\omega^4 = -1$
- Delta divisibility and factorization
- Ring membership checks

### Proof of Guaranteed SDE Reduction

Let

​							$$ \delta = 1 + \omega, \qquad\omega = e^{i\pi/4}$$


We prove that for any $q \in D[\omega]$, there exists $k \in \mathbb{N}$ such that

​						     		$$\delta^k q \in \mathbb{Z}[\omega].$$

Step 1: Compute $\delta^2$

​						$$ \delta^2 = (1+\omega)^2 = 1 + 2\omega + \omega^2 $$

Using the 8th root of unity relations:
$$
\omega^2 = i,
\qquad
\omega^4 = -1,
\qquad
\omega^8 = 1.
$$
From algebraic manipulation,
$$
\
\delta^2 = \sqrt{2}\,\lambda,
$$
​					        	where $\lambda \in \mathbb{Z}[\omega]$ is a unit.

Hence
$$
\sqrt{2} = \frac{\delta^2}{\lambda}.
$$
Step 2: Proof

Let
$$
\
q \in D[\omega].
$$
Then by definition of $D[\omega]$ there exists $z \in \mathbb{Z}[\omega]$ and $n \in \mathbb{N}$ such that
$$
q = \frac{z}{(\sqrt{2})^n}.
$$
Substitute $\sqrt{2} = \frac{\delta^2}{\lambda}$:
$$
\
q
=
\frac{z}{\left(\frac{\delta^2}{\lambda}\right)^n}
=
z \frac{\lambda^n}{\delta^{2n}}.
$$
Since $\lambda$ is a unit in $\mathbb{Z}[\omega]$,
$$
z\lambda^n \in \mathbb{Z}[\omega].
$$
Thus we may write
$$
\
q = \frac{z'}{\delta^{2n}}
\quad
\text{for some } z' \in \mathbb{Z}[\omega].
$$
Multiplying both sides by $\delta^{2n}$ gives
$$
\delta^{2n} q \in \mathbb{Z}[\omega].
$$
Let $k = 2n$. Then
$$
\
\delta^k q \in \mathbb{Z}[\omega].
$$
Conclusion:

For every $q \in D[\omega]$, there exists $k \in \mathbb{N}$ such that
$$
\delta^k q \in \mathbb{Z}[\omega].
$$

# References

[1] Aleks Kissinger and John van de Wetering, *Picturing Quantum Software*, Preprint Version 1.1.0 October 2024

[2] Vadym Kliuchnikov, Dmitri Maslov and Michele Mosca, *Fast and efficient exact synthesis of single qubit unitaries generated by Clifford and T gates*, arXiv:1206.5236v4 [quant-ph], 27 Feb 2013

# Acknowledgements

I would like to thank Amolak Kalra and Mark Deaconu for their guidance and generous help on this project.

I am especially grateful to Amolak Kalra for devoting so much time to mentor, encourage, and teach me the foundations that made this project possible.

Mark's related work helped in many ways in the completion of this project: https://github.com/MarkNDeaconu 

Finally I would like to thank Professor Mosca for giving me the opportunity of working in his quantum computing research group last summer.