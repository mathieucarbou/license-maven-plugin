
	ifdef	FOR80386

	.286c

	endif

utilasm_TEXT	SEGMENT	WORD PUBLIC 'CODE'
	ASSUME	CS:utilasm_TEXT


	ifdef	FOR80386

; Macro for 32-bit operand prefix.
OP32	macro
	db	66h
	endm

	endif					; FOR80386

; Clear a register

clear	macro	reg
	xor	reg,reg
	endm


	ifdef	FOR80386

; Replace the multiply and divide routines in the Turbo C library
; if we are running on an 80386.

; Macro to swap the halves of a 32-bit register.
; Unfortunately, masm won't allow a shift instruction with a count of 16,
; so we have to code it in hex.
swap	macro	regno
	  OP32
	db	0c1h,0c0h+regno,16		; rol regno,16
	endm
regax	equ	0
regcx	equ	1
regdx	equ	2
regbx	equ	3


