package com.example.easycheck_teacher;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewAttendanceAdpter extends RecyclerView.Adapter<ViewAttendanceAdpter.ViewHolder> {

    private List<Attendance> attendanceList;
    private Context context;
    private ViewAttendance activity;
    private Attendance attendance;

    public ViewAttendanceAdpter(Context context, List<Attendance> attendanceList,ViewAttendance activity) {
        this.context = context;
        this.attendanceList = attendanceList;
        this.activity=activity;
    }
    public void setAttendanceList(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
        notifyDataSetChanged();
    }
    public List<Attendance> getAttendanceList() {
        return attendanceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_attendance_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        attendance = attendanceList.get(position);

        // Bind data to the view elements
        holder.rollNoTextView.setText(attendance.getRoll());
        holder.nameTextView.setText(attendance.getName());
        holder.status_text_view1.setText(attendance.getStatus());
        holder.status_text_view2.setText(attendance.getStatus());
        holder.status_text_view3.setText(attendance.getStatus());
        if((attendance.getStatus()).equals("Present")){
            holder.status_text_view1.setVisibility(View.VISIBLE);
            holder.status_text_view2.setVisibility(View.GONE);
            holder.status_text_view3.setVisibility(View.GONE);
        }else if((attendance.getStatus()).equals("Absent")) {
            holder.status_text_view1.setVisibility(View.GONE);
            holder.status_text_view2.setVisibility(View.VISIBLE);
            holder.status_text_view3.setVisibility(View.GONE);
        }else{
            holder.status_text_view1.setVisibility(View.GONE);
            holder.status_text_view2.setVisibility(View.GONE);
            holder.status_text_view3.setVisibility(View.VISIBLE);
        }
        holder.attendanceCodeTextView.setText(attendance.getCode());
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView rollNoTextView;
        TextView nameTextView;
        TextView attendanceCodeTextView;
        TextView status_text_view1,status_text_view2,status_text_view3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find view elements by their IDs in your layout
            rollNoTextView = itemView.findViewById(R.id.roll_no_text_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            status_text_view1=itemView.findViewById(R.id.status_text_view1);
            status_text_view2=itemView.findViewById(R.id.status_text_view2);
            status_text_view3=itemView.findViewById(R.id.status_text_view3);
            attendanceCodeTextView = itemView.findViewById(R.id.attendance_code_text_view);
        }
    }
}
