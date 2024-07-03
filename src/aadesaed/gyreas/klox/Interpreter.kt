package aadesaed.gyreas.klox

import Token
import TokenType.*

@Suppress("UNREACHABLE_CODE")
class Interpreter : Visitor<Any?> {
    fun interpret(expression: Expr) {
        try {
            val value = evaluate(expression)
            println(stringify(value))
        } catch (error: RuntimeError) {
            runtimeError(error)
        }
    }

    override fun visitBinaryExpr(expr: Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double > right as Double
            }
            GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double >= right as Double
            }
            LESS -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double <= right as Double
            }
            LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double <= right as Double
            }
            BANG_EQUAL -> return !isEqual(left, right)
            EQUAL_EQUAL -> return isEqual(left, right)
            MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double - right as Double
            }
            PLUS -> {
                if (left is Double && right is Double) return left + right
                if (left is String && right is String) return left + right
                throw RuntimeError(expr.operator, "Operands  must be two numbers or two strings.")
            }
            SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double / right as Double
            }
            STAR -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double * right as Double
            }
            else -> {
                // Unreachable?
                throw RuntimeError(expr.operator, "Unsupported binary operator.")
            }
        }

        // Unreachable
        return null
    }

    override fun visitGroupingExpr(expr: Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Literal): Any? {
        return expr.value
    }

    override fun visitUnaryExpr(expr: Unary): Any {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            BANG -> !isTruthy(right)
            MINUS -> {
                checkNumberOperand(expr.operator, right)
                right as Double
            }
            else -> {}
        }
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operand must be numbers.")
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun isTruthy(any: Any?): Boolean {
        if (any == null) return false
        if (any is Boolean) return any
        return true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null) return false

        return a == b
    }

    private fun stringify(entity: Any?): String {
        if (entity == null) return "nil"

        if (entity is Double) {
            var asStr = entity.toString()
            if (asStr.endsWith(".0")) {
                asStr = asStr.substring(0..(asStr.length - 3))
            }
            return asStr
        }

        return "$entity"
    }
}

