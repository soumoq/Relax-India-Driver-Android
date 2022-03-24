package org.relaxindia.driver.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_notification_list.view.*
import org.json.JSONObject
import org.relaxindia.driver.NotificationApiModel
import org.relaxindia.driver.R
import org.relaxindia.driver.view.activity.NotificationActivity

import android.content.Intent
import android.net.Uri
import org.relaxindia.driver.view.activity.NotificationDetailsActivity


class NotificationAdapter(context: Context, isDashboard: Boolean = false) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    val context = context
    val isDashboard = isDashboard

    private var notiData: ArrayList<NotificationApiModel> = ArrayList()

    fun updateData(notiDatas: List<NotificationApiModel>) {
        this.notiData.clear()
        this.notiData.addAll(notiDatas)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_notification_list, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notiData[position], isDashboard)
    }

    override fun getItemCount() = notiData.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(notificationApiModel: NotificationApiModel, isDashboard: Boolean) {
            view.recycler_notification_create.text = notificationApiModel.created_at
            val obj = JSONObject(notificationApiModel.details)
            Log.e("JSONOBJ", obj.toString())
            view.noti_list_from.text = obj.getString("from_location")
            view.noti_list_to.text = obj.getString("to_location")

            if (isDashboard) {
                view.noti_action.visibility = View.GONE
            } else {
                view.noti_action.visibility = View.VISIBLE
            }


            view.noti_list_accept.setOnClickListener {
                (view.context as NotificationActivity).bookOrder(obj.getString("id"))
            }

            view.noti_list_reject.setOnClickListener {
                (view.context as NotificationActivity).rejectBooking(obj.getString("id"))
            }

            view.noti_layout.setOnClickListener {
                val intent = Intent(view.context, NotificationDetailsActivity::class.java)
                intent.putExtra("noti_details", notificationApiModel.details)
                intent.putExtra("user_name", notificationApiModel.userName)
                intent.putExtra("user_email", notificationApiModel.userEmail)
                intent.putExtra("user_phone", notificationApiModel.userPhone)
                intent.putExtra("created_at", notificationApiModel.created_at)
                intent.putExtra("booking_id", notificationApiModel.bookingId.toString())
                intent.putExtra("activity", notificationApiModel.activity)
                intent.putExtra("is_reached", notificationApiModel.isReached.toString())
                Log.e("ASQDWEQ", notificationApiModel.isReached.toString())

                view.context.startActivity(intent)
            }

        }
    }
}
