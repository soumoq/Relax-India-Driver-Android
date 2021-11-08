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

            view.locate_googlemap.setOnClickListener {
                val strUri =
                    "http://maps.google.com/maps?q=loc:" + obj.getString("user_latitude") + "," + obj.getString(
                        "user_longitude"
                    ) + " (" + "Label which you want" + ")"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUri))
                intent.setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
                )
                view.context.startActivity(intent)
            }

            view.noti_list_accept.setOnClickListener {
                (view.context as NotificationActivity).bookOrder(obj.getString("id"))
            }

            view.noti_list_reject.setOnClickListener {
                (view.context as NotificationActivity).rejectBooking(obj.getString("id"))
            }
        }
    }
}
