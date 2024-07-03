package aadesaed.gyreas.tool

import Token
import TokenType.MINUS
import TokenType.STAR
import aadesaed.gyreas.klox.*

class AstPrinter : Visitor<String> {
    private val INDENT_WIDTH = 2
    private var indentLevel = 0

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

            if (indentLevel > 0) append("\n")
            append(" ".repeat(indentLevel))
            append("($name")

            indentLevel += INDENT_WIDTH

            exprs.forEach { expression ->
                append(" ")
                append(expression.accept(printer))
            }
            append(")")
            if (indentLevel == 0) append("\n")

            indentLevel -= INDENT_WIDTH
        }
    }
}

fun main() {
    val expression = Binary(
        Grouping(
            Binary(
                Literal(1), Token(MINUS, "+", null, 1), Literal(2)
            )
        ), Token(STAR, "*", null, 1), Grouping(
            Binary(
                Literal(4), Token(MINUS, "-", null, 1), Literal(3)
            )
        )
    )

    println(AstPrinter().print(expression))
}