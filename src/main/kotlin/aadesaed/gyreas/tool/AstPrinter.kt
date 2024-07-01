package aadesaed.gyreas.tool

import Token
import TokenType.MINUS
import TokenType.STAR
import aadesaed.gyreas.klox.*

class AstPrinter : Visitor<String> {
    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    override fun visitBinaryExpr(expr: Binary): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitGroupingExpr(expr: Grouping): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: Literal): String {
        if (expr.value == null) return "nil"
        return expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val printer = this

        return buildString {
            append("($name")
            exprs.forEach { expression ->
                append(" ${expression.accept(printer)}")
            }
            append(")")
        }
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

    println(AstPrinter().print(expression))
}