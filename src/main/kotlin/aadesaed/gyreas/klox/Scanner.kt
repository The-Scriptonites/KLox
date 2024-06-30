import TokenType.*
import aadesaed.gyreas.klox.error

class Scanner(private val source: String) {
    private var tokens = ArrayList<Token>()
    private var start = 0
    private var current = 0
    private var line = 1
//    private var commentDepth = 0

    private val keywords: HashMap<String, TokenType> = HashMap()

    init {
        keywords["and"] = AND;
        keywords["class"] = CLASS
        keywords["else"] = ELSE
        keywords["false"] = FALSE
        keywords["for"] = FOR
        keywords["fun"] = FUN
        keywords["if"] = IF
        keywords["nil"] = NIL
        keywords["or"] = OR
        keywords["print"] = PRINT
        keywords["return"] = RETURN
        keywords["super"] = SUPER
        keywords["this"] = THIS
        keywords["true"] = TRUE
        keywords["var"] = VAR
        keywords["while"] = WHILE
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    fun scanTokens(): ArrayList<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        when (val c: Char = advance()) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addToken(if (matchNext('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (matchNext('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (matchNext('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (matchNext('=')) GREATER_EQUAL else GREATER)
            '/' -> comment()

            ' ', '\r', '\t' -> nop()
            '\n' -> line++

            '"' -> string()

            else -> when {
                isDigit(c) -> number()
                isAlpha(c) -> identifier()
                else -> error(line, "Unexpected character: '$c'")
            }
        }
    }

    private fun comment() {
        if (matchNext('/')) {
            while (peek() != '\n' && !isAtEnd()) advance()
        } else if (matchNext('*')) {
            // Skip the comment content
            while (!isAtEnd()) {
                if (peek() == '*') {
                    if (peekNext() == '/') break
                    advance()
                }
                if (peek() == '\n') line++
                advance()
            }

            if (isAtEnd()) {
                error(line, "Unterminated comment.")
                return
            }

            // The closing *
            advance()
            // The closing /
            advance()
        } else {
            addToken(SLASH)
        }
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        val slice = source.substring(start, current)
        val tkType = keywords[slice] ?: IDENTIFIER
        addToken(tkType)
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            error(line, "Unterminated string literal.")
            return;
        }

        // The closing "
        advance()

        // Trim the surrounding quotes
        val value = source.substring(start + 1, current - 1)
        addToken(STRING, value)
    }

    private fun number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, source.substring(start, current).toDouble());
    }

    private fun nop() {}

    private fun matchNext(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    private fun isAlpha(c: Char): Boolean {
        return (c in 'a'..'z') || (c in 'A'..'Z') || c == '_'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun addToken(tkType: TokenType) {
        addToken(tkType, null)
    }

    private fun addToken(tkType: TokenType, literal: Any?) {
        val slice = source.substring(start, current)
        tokens.add(Token(tkType, slice, literal, line))
    }
}
