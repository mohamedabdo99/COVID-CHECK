package com.mohamed.abdo.myhealth.utils


import com.mohamed.abdo.myhealth.utils.Constants.OPEN_GOOGLE
import com.mohamed.abdo.myhealth.utils.Constants.OPEN_SEARCH
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

object BotResponse {

    fun basicResponses(_message: String): String {

        val random = (0..2).random()
        val message =_message.toLowerCase()

        return when {
/*
//            //Flips a coin
//            message.contains("flip") && message.contains("coin") -> {
//                val r = (0..1).random()
//                val result = if (r == 0) "heads" else "tails"
//
//                "I flipped a coin and it landed on $result"
//            }
    */
            //prevent the spread of COVID-19
            message.contains("prevent") -> {
                when (random) {
                    0 -> "Maintain a safe distance from others (at least 1 metre), even if they don’t appear to be sick.!"
                    1 -> "Wear a mask in public, especially indoors or when physical distancing is not possible"
                    2 -> "Choose open, well-ventilated spaces over closed ones. Open a window if indoors!"
                    else -> "error" }
            }

            //i reduce my risk of getting covide
            message.contains("reduce") -> {
                when (random) {
                    0 -> "Follow local guidance: Check to see what national, regional and local authorities are advising so you have the most relevant information for where you are."
                    1 -> "Keep your distance: Stay at least 1 metre away from others, even if they don’t appear to be sick, since people can have the virus without having symptoms."
                    2 -> "Avoid crowded places, poorly ventilated, indoor locations and avoid prolonged contact with others. Spend more time outdoors than indoors."
                    else -> "error" }
            }
            // When I am very idle, do I isolate myself?
            message.contains("idle") -> {
                when (random) {
                    0 -> "Yes, take care of your relatives and your family"
                    1 -> "Go to your doctor to be sure"
                    2 -> "Stay at home until symptoms are confirmed"
                    else -> "error" }
            }
            //What are the symptoms of Corona?
            message.contains("symptoms") -> {
                when (random) {
                    0 -> "Most common symptoms: fever "
                    1 -> "Most common symptoms: cough "
                    2 -> "Most common symptoms: loss of taste or smell "
                    else -> "error" }
            }
      /*
            //Math calculations
            message.contains("solve") -> {
                val equation: String? = message.substringAfterLast("solve")
                return try {
                    val answer = SolveMath.solveMath(equation ?: "0")
                    "$answer"

                }
                catch (e: Exception) {
                    "Sorry, I can't solve that."
                }
            }
            */

            //Hello
            message.contains("hello") -> {
                when (random) {
                    0 -> "Hello there!"
                    1 -> "Sup"
                    2 -> "Buongiorno!"
                    else -> "error" }
            }

            //How are you?
            message.contains("how are you") -> {
                when (random) {
                    0 -> "I'm doing fine, thanks!"
                    1 -> "I'm hungry..."
                    2 -> "Pretty good! How about you?"
                    else -> "error"
                }
            }

            //What time is it?
            message.contains("time") && message.contains("?")-> {
                val timeStamp = Timestamp(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val date = sdf.format(Date(timeStamp.time))

                date.toString()
            }

            //Open Google
            message.contains("open") && message.contains("google")-> {
                OPEN_GOOGLE
            }

            //Search on the internet
            message.contains("search")-> {
                OPEN_SEARCH
            }

            //When the programme doesn't understand...
            else -> {
                when (random) {
                    0 -> "I don't understand..."
                    1 -> "Try asking me something different"
                    2 -> "Idk"
                    else -> "error"
                }
            }
        }
    }
}