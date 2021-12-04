package org.relaxindia.driver.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.schedule_booking_list.view.*

import org.relaxindia.driver.R
import org.relaxindia.driver.model.ScheduleBookingModel


class ScheduleBookingAdapter(context: Context) :
    RecyclerView.Adapter<ScheduleBookingAdapter.ViewHolder>() {
    val context = context

    private var booking: ArrayList<ScheduleBookingModel> = ArrayList()

    fun updateData(book: List<ScheduleBookingModel>) {
        this.booking.clear()
        this.booking.addAll(book)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.schedule_booking_list, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(booking.get(position), context)
    }

    override fun getItemCount() = booking.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(booking: ScheduleBookingModel, context: Context) {

            view.success_schedule_source_loc.text = booking.from_location
            view.success_schedule_des_loc.text = booking.to_location
            view.success_schedule_booking_status.text = "Status : ${booking.status}"
            view.schedule_booking_user_name.text = "User Name : ${booking.user_name}"
            view.schedule_booking_user_phone.text = "User Phone Number : ${booking.user_phone}"

            if (booking.user_comment != "null")
                view.success_schedule_booking_comment.text =
                    "User comment : ${booking.user_comment}"
            view.success_schedule_booking_date_time.text = "Date Time : ${booking.date}"
            view.booking_amount.text = booking.booking_amount.toString()
            view.total_amount.text = booking.total_amount.toString()


        }
    }
}
