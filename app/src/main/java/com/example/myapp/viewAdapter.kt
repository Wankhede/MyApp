package com.example.myapp

import android.content.Context
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.util.zip.Inflater

class viewAdapter(val notesid: List<String>, val notesdescription: List<String>) : RecyclerView.Adapter<viewAdapter.viewHolder>(){

    var idArrayList = ArrayList<String>()
    var desArrayList = ArrayList<String>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.frame_textview, parent, false)

        idArrayList = getList("noteiddd", requireContext())
        desArrayList = getList("notedisss", requireContext())

        val holder = viewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val model = idArrayList[position]

        }
        return viewHolder(view)

    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.noteid.text = notesid[position]
        holder.notedescriptor.text = notesdescription[position]

        holder.remove.setOnClickListener {
            notesid.o
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,notesid.size)
        }
        holder.remove.setOnClickListener {
            notesdescription.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,notesdescription.size)
        }

    }

    override fun getItemCount(): Int {
        //TODO("Not yet implemented")
        return notesid.size
    }
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var noteid = itemView.findViewById<TextView>(R.id.NoteId)
        var notedescriptor = itemView.findViewById<TextView>(R.id.NoteDescription)
        val remove: ImageView = itemView.findViewById(R.id.delete_btn)
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


