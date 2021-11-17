package com.example.myapp

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class NotesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var recyclerView: RecyclerView? = null

    var add: FloatingActionButton? = null
    var add_Note_Layout: ConstraintLayout? = null
    var back: ImageView? = null
    var backtoact: ImageView? = null
    var save: Button? = null
    var delete: ImageView? = null
    var newidadd: EditText? = null
    var newdesadd: EditText? = null
    var MY_SHARED_PREF_NAME = "Note creator"
    var sharedPreference: SharedPreferences? = null
    var id: String? = null
    var dis: String? = null
    var idArrayList = ArrayList<String>()
    var desArrayList = ArrayList<String>()


    //var idArrayList : ArrayList<String>? = null
    //var desArrayList : ArrayList<String>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val v = inflater.inflate(R.layout.fragment_notes, container, false)

        add = v.findViewById(R.id.addbtn)
        add_Note_Layout = v.findViewById(R.id.add_note_layout)
        save = v.findViewById(R.id.save_btn)
        delete = v.findViewById(R.id.delete_btn)
        newidadd = v.findViewById(R.id.Note_title)
        newdesadd = v.findViewById(R.id.Note_description)

        recyclerView = v.findViewById(R.id.recyclerview)

        sharedPreference =
            requireContext().getSharedPreferences("MY_SHARED_PREF_NAME", Context.MODE_PRIVATE)

        idArrayList = getList("noteiddd", requireContext())
        desArrayList = getList("notedisss", requireContext())

        Log.d(TAG, "onCreateView: get id array " + idArrayList)
        Log.d(TAG, "onCreateView: get des array " + desArrayList)

        id = sharedPreference!!.getString("noteiddd", "abc")
        dis = sharedPreference!!.getString("notedisss", "abcdef")

        val notesids = idArrayList
        val notesdescriptions = desArrayList

        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = viewAdapter(notesids, notesdescriptions)

        add_Note_Layout!!.visibility = View.GONE
        save!!.visibility = View.VISIBLE


        add!!.setOnClickListener {
            add_Note_Layout!!.visibility = View.VISIBLE
        }

        save!!.setOnClickListener {
            val noteidd = newidadd!!.text.trim().toString()
            val notediss = newdesadd!!.text.trim().toString()

            if (noteidd.isEmpty() || notediss.isEmpty()) {
                showCustomDialog(
                    requireContext(),
                    R.string.incomplete_information_title,
                    R.string.incomplete_board_name,
                    R.string.ok
                )
            } else {
                add_Note_Layout!!.visibility = View.GONE


                idArrayList!!.add(noteidd)
                desArrayList!!.add(notediss)
                putList("noteiddd", idArrayList!!, requireContext())
                putList("notedisss", desArrayList!!, requireContext())

                val notesids = idArrayList
                val notesdescriptions = desArrayList

                recyclerView!!.layoutManager = LinearLayoutManager(context)
                recyclerView!!.adapter = viewAdapter(notesids, notesdescriptions)


                // editor.apply()
            }
        }

        recyclerView!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        }

        return v

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun showCustomDialog(context: Context, title: Int, msg: Int, btn: Int) {

        val dialog = Dialog(context)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        // Include dialog.xml file
        dialog.setContentView(R.layout.alert_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val textView = dialog.findViewById(R.id.dialog_title) as TextView
        textView.setText(title)

        // textView.typeface = REGULAR

        // set values for custom dialog components - text, image and button
        val text = dialog.findViewById(R.id.dialog_name) as TextView
        text.setText(msg)
        //  text.typeface = LIGHT

        val declineButton = dialog.findViewById(R.id.btn_ok) as Button
        declineButton.setText(btn)
        //  declineButton.typeface = LIGHT
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener {
            // Close dialog
            dialog.dismiss()
        }
        dialog.show()
    }

    fun putList(mtname: String, notes: ArrayList<String>, context: Context) {

        //add the value in shared prefence
        val sPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sPrefs.edit()

        val jArray = JSONArray(notes)
        editor.remove(mtname)
        editor.putString(mtname, jArray.toString())
        editor.apply()
    }

    fun getList(mtname: String, context: Context): ArrayList<String> {

        val sPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sPrefs.edit()

        val array = ArrayList<String>()
        val jArrayString = sPrefs.getString(mtname, "NOPREFSAVED")
        //  String jArrayString=AppUtil.getString("mtname",context);

        try {
            val DataArray = JSONArray(jArrayString)
            for (p in 0 until DataArray.length()) {
                array.add(DataArray.getString(p))
                //   Log.e("data",""+array.get(p));
            }

        } catch (ignored: Exception) {

        }

        return array
    }

}