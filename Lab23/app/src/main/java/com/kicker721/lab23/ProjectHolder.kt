package com.kicker721.lab23

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.net.toUri

class ProjectAdapter(val projects: List<Project>) :
        RecyclerView.Adapter<ProjectAdapter.ProjectHolder>() {
    class ProjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tvName)
        var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        var tvLanguage: TextView = itemView.findViewById(R.id.tvLanguage)
        var tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProjectHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_project, parent, false)
        return ProjectHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectHolder, position: Int) {
        holder.tvName.text = projects[position].name
        holder.tvLanguage.text = projects[position].language
        holder.tvDescription.text = projects[position].description
        holder.tvName.setOnClickListener {
            val url = projects[position].url
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, url.toUri())
            holder.itemView.context.startActivity(intent)
        }
        holder.tvAuthor.text = projects[position].author
        holder.tvAuthor.setOnClickListener {
            val url = projects[position].authorUrl
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, url.toUri())
            holder.itemView.context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return projects.size
    }
}