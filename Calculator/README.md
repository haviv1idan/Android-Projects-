# ***Calculator!***
> Hi Everyone.
> In this Project you can see my calculator.

# ***What speicel in my calculator?***
> In this project i created a calculator that calculate expressions by himself with my algorithm.
> In MainActivity you can see the full algorithm.
> Here is a taste of it 

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


Have fun 😁
