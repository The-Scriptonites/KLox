package aadesaed.gyreas.klox

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    println("./klox ${args.joinToString()}\n")

    if (args.size > 1) {
        println("usage: klox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        runFile(args[0])
    } else {
        val testSource =
            """
            // this is a comment
            (()){} // grouping stuff
            !* + -/=<> <= == // operators
            """
//        run(testSource)
        runPrompt();
    }
}

fun runFile(filePath: String) {
    val bytes = Files.readAllBytes(Paths.get(filePath))
    run(String(bytes))
    if (hadError) exitProcess(65)
}

fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
        print("> ")
        val line = reader.readLine() ?: break
        run(line)
        hadError = false
    }
}

fun run(source: String) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    for (token in tokens) {
        println(token.toString())
    }
}

fun error(line: Int, message: String) {
    report(line, "", message)
}

var hadError = false
fun report(line: Int, where: String, message: String) {
    System.err.println("[line $line] Error$where: $message")
    hadError = true
}