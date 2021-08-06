package com.example.calculatorv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var textviewExercise: TextView
    private lateinit var textviewAnswer: TextView
    private var isFirstOperatorClicked = false

    // operators functions
    val multiply = {n1: Double, n2: Double -> n1 * n2}
    val division = {n1: Double, n2: Double -> n1 / n2}

    private val operators = listOf('+','-','X','/','%')

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textviewExercise = findViewById(R.id.textview_exercise)
        textviewAnswer = findViewById(R.id.textview_answer)
    }

    /*
        status:
            organize a string with digits and operators
            need to add: x^y , () , %
     */
    private fun organizeExpression(exp: String): MutableList<String>{
        /*
        The function gets an expression string
        and returns a Mutable list of numbers and operators
        in order of expression
        param exp: expression string to calculate
         */

        // we will create Mutable list for the expression
        val lst: MutableList<String> = mutableListOf()

        var startNumPointer = 0
        var num = ""

        // run on exp string
        for ((index, value) in exp.withIndex()) {

            // if the first number is negative
            if (index == 0 && value == '-')
                continue
                // if found operator
            else if (value in operators && exp[index - 1] != '(') {

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

                // reset num
                num = ""

            } else if (value == '(')
                    startNumPointer += 1
            }
        // end of for loop
        // this loop is for last number
        for (i in startNumPointer until exp.length) {
            num += exp[i].toString()
        }
        // adding last number to lst
        lst.add(num)
        return lst
    }


    // finished
    private fun calculateSequence(sequence: MutableList<String>): Double {
        /*
        The function calculate a sequence of X and /.
        The function returns the result of sequence
         */

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

    // finished
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

    // finished
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

    /*
    status:
        * need to add x^y , () , %
    */
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

        println("result: $result")
        return result
    }

    // finished
    fun onClickDigit(view: View) {
        textviewExercise.append((view as Button).text)
        // start solve when have at least one operator
        if (isFirstOperatorClicked)
            solve(textviewExercise.text.toString())
    }

    // finished
    fun onClickOperator(view: View) {
        // cannot append operator as first or last
        if (textviewExercise.text != "" && textviewExercise.text.last() !in operators) {
            textviewExercise.append((view as Button).text)
            isFirstOperatorClicked = true
        }
    }

    // status: need add: when clicked add exercise and answer to history list
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

    // finished
    fun onClickAc(view: View){
        textviewExercise.text = ""
        textviewAnswer.text = ""
        isFirstOperatorClicked = false
    }

    // finished
    fun onClickBackspace(view: View){
        // get the expression
        var txt = textviewExercise.text
        println("txt: $txt")

        when {
            txt.isEmpty() -> return
            txt.length == 1 -> textviewExercise.text = ""
            else -> {
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

    // finished
    private fun addMinus(exp: String): String {
        /*
        The function change the last number from positive to negative
        */
        var newExp = ""
        var lastOperatorIndex = 0
        for (i in exp.length - 1 downTo 0) {
            if (exp[i] in operators) {
                lastOperatorIndex = i
                break
            }
        }
        if (lastOperatorIndex == 0) {
            newExp = "-"
            for (i in exp)
                newExp += i.toString()
        } else {
            for (i in 0 until lastOperatorIndex+1)
                newExp += exp[i].toString()
            newExp += "(-"
            for (i in lastOperatorIndex+1 until exp.length)
                newExp += exp[i].toString()
        }
        println("newExp: $newExp")
        return newExp
    }

    // finished
    fun onClickPlusMinus(view: View) {
        var txt = textviewExercise.text
        var newExp = ""

        // start searching from end operator
        for (i in txt.length-1 downTo 0){

            // if the operator is '-' and before him have '('.
            // we need to clear them
            if (txt[i] == '-' && txt[i-1] == '(') {

                // this for clear the '(-' from txt and put the num exp in newExp
                for (i in txt.length - 1 downTo 0)

                    if (txt[i] == '(') {

                        for (j in 0 until i)
                            newExp += txt[j].toString()
                        for (j in i + 2 until txt.length)
                            newExp += txt[j].toString()
                        break
                    }
                textviewExercise.text = newExp
                solve(textviewExercise.text.toString())
                return
            }
            // else if has only operator we need to add minus
            else if (txt[i] in operators){
                textviewExercise.text = addMinus(txt.toString())
                solve(textviewExercise.text.toString())
                return
            }
        }
        textviewExercise.text = addMinus(txt.toString())
    }
}
