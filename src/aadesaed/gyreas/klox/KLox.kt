package aadesaed.gyreas.klox

import Token
import TokenType
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("usage: klox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        runFile(args[0])
    } else {
        runPrompt()
    }
}

val interpreter = Interpreter()
var hadError = false
var hadRuntimeError = false

fun runFile(filePath: String) {
    val bytes = Files.readAllBytes(Paths.get(filePath))

    run(String(bytes))

    if (hadError) exitProcess(65)
    if (hadRuntimeError) exitProcess(70)
}

fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
        print("> ")

        val line = reader.readLine() ?: break

        run(line)

        hadError = false
        hadRuntimeError = false
    }
}

fun run(source: String) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()
    val parser = Parser(tokens)
    val expression = parser.parse()

    if (hadError) return

    interpreter.interpret(expression!!)
}

fun error(line: Int, message: String) {
    report(line, "", message)
}

fun report(line: Int, where: String, message: String) {
    System.err.println("[line $line] Error$where: $message")
    hadError = true
}

fun error(token: Token, message: String?) {
    if (token.type === TokenType.EOF) {
        report(token.line, " at end", message!!)
    } else {
        report(token.line, (" at '" + token.lexeme).toString() + "'", message!!)
    }
}

fun runtimeError(error: RuntimeError) {
    System.err.println(
            """
        ${error.message}
        [line${error.token.line}]
        """.trimIndent()
    )
    hadRuntimeError = true
}

