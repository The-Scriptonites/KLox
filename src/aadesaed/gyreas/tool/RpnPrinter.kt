package aadesaed.gyreas.tool

import Token
import TokenType.MINUS
import TokenType.STAR
import aadesaed.gyreas.klox.*

class RpnPrinter : Visitor<String> {
    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    private fun rpn(name: String, vararg exprs: Expr): String {
        val printer = this

        return buildString {
            append("(")
            exprs.forEach { expr ->
                append("${expr.accept(printer)} ")
            }
            append("$name)")
        }
    }

    override fun visitBinaryExpr(expr: Binary): String {
        return rpn(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitGroupingExpr(expr: Grouping): String {
        return expr.expression.accept(this)
    }

    override fun visitLiteralExpr(expr: Literal): String {
        if (expr.value == null) return "nil"
        return expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Unary): String {
        return "${expr.operator.lexeme}${expr.right.accept(this)}"
    }
}

fun main(args: Array<String>) {
    val expression = Binary(
        Grouping(
            Binary(
                Literal(1),
                Token(MINUS, "+", null, 1),
                Literal(2)
            )
        ),
        Token(STAR, "*", null, 1),
        Grouping(
            Binary(
                Literal(4),
                Token(MINUS, "-", null, 1),
                Literal(3)
            )
        )
    )

    println(RpnPrinter().print(expression))
}
