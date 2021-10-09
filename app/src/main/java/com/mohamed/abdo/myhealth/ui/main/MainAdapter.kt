package com.mohamed.abdo.myhealth.ui.main

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mohamed.abdo.myhealth.R


class MainAdapter():RecyclerView.Adapter<MainAdapter.ViewHolder>()
{
    lateinit var btnConfirm: Button
    lateinit var context: Context
    private var listener: OnClickConfirm? = null
    private var layoutInflater: LayoutInflater? = null

    fun setOnItemClickListener(mListener: OnClickConfirm){
        this.listener = mListener
    }

    var questionList:List<String> = listOf(
        "Have you been in contact with someone infected with corona before",
        "Do you have breathing difficulties ?",
        "Do you have a high temperature of more than 38 degrees ?"
    )
    var questionNumber : List<Int> = listOf(1, 2, 3)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapter.ViewHolder {
        if (layoutInflater == null)
        {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_home_question, parent, false
        )
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.tvQuestionKtx.text = questionList[position]
        holder.tvNumberQuestionKtx.text = questionNumber[position].toString()
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var tvNumberQuestionKtx : TextView
        var tvQuestionKtx : TextView
        var btnYes : Button
        var btnNo : Button
        init {
            tvNumberQuestionKtx = itemView.findViewById(R.id.tvNumberQuestion)
            tvQuestionKtx = itemView.findViewById(R.id.tvQuestion)
            btnYes = itemView.findViewById(R.id.btnYes)
            btnNo = itemView.findViewById(R.id.btnNo)
            onItemClick()
        }
        private fun onItemClick()
        {
            btnYes.setOnClickListener{ v->
                listener?.onClick("yes", bindingAdapterPosition)
                v.setBackgroundColor(Color.RED)
                btnNo.setBackgroundColor(Color.TRANSPARENT)
            }
            btnNo.setOnClickListener{ v->
                listener?.onClick("no", bindingAdapterPosition)
                v.setBackgroundColor(Color.RED)
                btnYes.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}