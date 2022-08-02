package com.woohoo.schedule.ui.more

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.woohoo.schedule.R
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.woohoo.schedule.about
import com.woohoo.schedule.trash
import com.woohoo.schedule.archive

class MoreFragment : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val root = inflater.inflate(R.layout.fragment_more, container, false)
        val bt_about = root.findViewById<ConstraintLayout>(R.id.Btn_About)
        val bt_trash = root.findViewById<ConstraintLayout>(R.id.btn_Trash)
        val bt_archive = root.findViewById<ConstraintLayout>(R.id.btn_Archive)
        val bt_feedback = root.findViewById<ConstraintLayout>(R.id.constraintLayout3_1)
        val bt_share = root.findViewById<ConstraintLayout>(R.id.btn_share)
        bt_about.setOnClickListener { view: View? ->
            val intent = Intent()
            intent.setClass(requireActivity(), about::class.java) //RequireActivity is the current Activty and class is preceded by the name of the activity to jump to
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.trans_in, R.anim.no_anim)
        }
        bt_trash.setOnClickListener { view: View? ->
            val intent = Intent()
            intent.setClass(requireActivity(), trash::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.trans_in, R.anim.no_anim)
        }
        bt_archive.setOnClickListener { view: View? ->
            val intent = Intent()
            intent.setClass(requireActivity(), archive::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.trans_in, R.anim.no_anim)
        }
        bt_feedback.setOnClickListener { view: View? ->
            val uri = Uri.parse("mailto:")
            val email = arrayOf("mail@mail.com")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra(Intent.EXTRA_EMAIL, email)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback:WooHoo Schedule Feedback") // theme
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent.createChooser(intent, "Please select the Mail application"))
        }
        bt_share.setOnClickListener { view: View? -> shareText(activity, "WooHoo Schedule") }
        return root
    }

    companion object
    {
        fun shareText(context: Context?, extraText: String?)
        {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share this App")
            intent.putExtra(Intent.EXTRA_TEXT, extraText) //ExtraText for text content
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK //Create a new task stack for the Activity
            context!!.startActivity(
                    Intent.createChooser(intent, "Please select the application to share")) //R.string.action_share also a title
        }
    }
}