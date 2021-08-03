package com.example.calculatorv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var textviewExercise: TextView
    private lateinit var textviewAnswer: TextView
    private var isFirstOperatorClicked: Boolean = false

    // operators functions
    val multiply = {n1: Double, n2: Double -> n1 * n2}
    val division = {n1: Double, n2: Double -> n1 / n2}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textviewExercise = findViewById(R.id.textview_exercise)
        textviewAnswer = findViewById(R.id.textview_answer)
    }

    private fun organizeExpression(exp: String): MutableList<String>{
        /*
        The function gets an expression string
        and returns a Mutable list of numbers and operators
        in order of expression
        param exp: expression string to calculate
         */

        // we will create Mutable list for the expression
        val lst: MutableList<String> = mutableListOf()

        val operators = listOf('+','-','X','/')
        var startNumPointer = 0
        var num = ""

        // run on exp string
        for ((index, value) in exp.withIndex()) {
            // if found operator
            if (value in operators) {
                // run all chars from last startNunPointer until index - 1
                for (i in startNumPointer until index) {
                    num += exp[i].toString()
                }
                // adding num to lst
                lst.add(num)
                // adding operator to lst
                lst.add(value.toString())
                // increase startNumPointer to number after operator
                startNumPointer = index + 1
                num = ""
            }
        }
        // this loop is for last number
        for (i in startNumPointer until exp.length) {
            num += exp[i].toString()
        }
        // adding last number to lst
        lst.add(num)
        return lst
    }

    private fun calculateSequence(sequence: MutableList<String>): Double {

        var res: Double
        if (sequence.size == 3)
            res = if (sequence[1] == "X")
                multiply(sequence[0].toDouble(), sequence[2].toDouble())
            else
                division(sequence[0].toDouble(), sequence[2].toDouble())
        else {
            res = sequence[0].toDouble()
            for (i in 1 until sequence.size - 1 step 2) {
                println("i = $i")
                res = if (sequence[i] == "X")
                    multiply(res, sequence[i + 1].toDouble())
                else
                    division(res, sequence[i + 1].toDouble())
            }
        }
        return res
    }

    private fun solveMultiplyAndDivision(organizedExp: MutableList<String>): MutableList<String> {
        /*
        The function goes over organizedExp and looking for X and /
        when we found one of them, the function take the number before
        and the number after the operator and put the result in newExp.
        param organizedExp: include the expression
         */
        val newExp: MutableList<String> = mutableListOf()

        // copy organizedExp to newExp
        for ((i,v) in organizedExp.withIndex()){
            newExp.add(i, v)
            }

        var res: Double
        var pointerOrganizedExp = 0
        var pointerNewExp: Int

        for ((index, value) in organizedExp.withIndex()) {

            /* this condition is for after we found a sequence of multiples or Divisions.
             we wont go back to middle of sequence, so as long as index is in sequence
             we need to continue until index will equals to pointerOrganizedExp
            */
            if (pointerOrganizedExp > index)
                continue

            else if ((value == "X" || value == "/") && (index == pointerOrganizedExp)) {

                val sequence: MutableList<String> = mutableListOf(organizedExp[index - 1])
                // check if there is sequence of X and /
                while ((pointerOrganizedExp < organizedExp.size) && (organizedExp[pointerOrganizedExp] == "X" || organizedExp[pointerOrganizedExp] == "/")) {
                    // we jump by 2 because the operators are in jump of 2
                    pointerOrganizedExp += 2
                    }

                for (i in index until pointerOrganizedExp){
                    sequence.add(organizedExp[i])
                }
                println("sequence: $sequence")
                res = calculateSequence(sequence)

                println("final res: $res")
                // the index of value
                pointerNewExp = newExp.indexOf(value)
                println("pointer new Exp: $pointerNewExp")
                newExp.add(pointerNewExp - 1, res.toString())
                println("newExp before: $newExp")
                for (i in 1..sequence.size)
                    newExp.removeAt(pointerNewExp)
            }
            else
                // if value isn't operator X or / keep increase pointer with index
                pointerOrganizedExp++
        }
        return newExp
    }

    private fun solveAddAndSub(exp: MutableList<String>): Double {

        var i = 0

        // set the first number as res
        var res = exp[i].toDouble()
        i++

        while (i < exp.size){
            if (exp[i] == "+") {
                res += exp[i + 1].toDouble()
                i += 2
            }
            else{
                res -= exp[i + 1].toDouble()
                i += 2
            }
        }
        return res
    }

    private fun solve(exp: String): Double{

        println("first Exp: $exp")

        // first of all we need to organize the number and operators
        val organizedExp: MutableList<String> = organizeExpression(exp)
        println("organized Exp: $organizedExp")

        // after that we organized the list we need to calculate
        // first the multiply and divisions
        val newExp: MutableList<String> = solveMultiplyAndDivision(organizedExp)
        println("new Exp: $newExp")

        // final step is calculate the result of add and subs
        val result: Double = solveAddAndSub(newExp)
        if (result.toString().substring(result.toString().length-2,result.toString().length) == ".0")
            textviewAnswer.text = result.toInt().toString()
        else
            textviewAnswer.text = result.toString()

        return result
    }

    fun onClickDigit(view: View) {
        textviewExercise.append((view as Button).text)
        if (isFirstOperatorClicked)
            solve(textviewExercise.text.toString())
    }

    fun onClickOperator(view: View) {
        textviewExercise.append((view as Button).text)
        isFirstOperatorClicked = true
    }

    fun onClickEqual(view: View){
        // Read the Expression
        val txt = textviewExercise.text.toString()
        // val txt = "5X5+5" V
        // val txt = "5+5X5" V
        // val txt = "5X5X5" V
        // val txt = "5+5X5X5-6X3X2+8/4" V
        val result = solve(txt)
        if (result.toString().substring(result.toString().length-2,result.toString().length) == ".0")
            textviewExercise.text = result.toInt().toString()
        else
            textviewExercise.text = result.toString()
        textviewAnswer.text = ""
    }

    fun onClickAc(view: View){
        textviewExercise.text = ""
        textviewAnswer.text = ""
        isFirstOperatorClicked = false
    }

    fun onClickBackspace(view: View){
        // get the expression
        var txt = textviewExercise.text

        if (txt != "") {
            // convert expression to MutableList
            val txt2 = txt.toMutableList()
            // remove last element
            txt2.removeLast()
            // convert back Mutable list to String
            txt = ""
            for (i in txt2) {
                txt = txt.toString() + i.toString()
            }
            println(txt.toString())
            textviewExercise.text = txt
        }
    }
}
