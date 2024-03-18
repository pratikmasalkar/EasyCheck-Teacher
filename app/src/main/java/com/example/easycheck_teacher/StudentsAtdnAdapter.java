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

public class StudentsAtdnAdapter extends RecyclerView.Adapter<StudentsAtdnAdapter.ViewHolder> {

    private List<Attendance> attendanceList;
    private Context context;
    private MarkAttendance activity;
    private Attendance attendance;

    public StudentsAtdnAdapter(Context context, List<Attendance> attendanceList,MarkAttendance activity) {
        this.context = context;
        this.attendanceList = attendanceList;
        this.activity=activity;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_row_layout, parent, false);
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
        if((attendance.getStatus()).equals("Present")){
            holder.status_text_view1.setVisibility(View.VISIBLE);
            holder.status_text_view2.setVisibility(View.GONE);
        }else {
            holder.status_text_view1.setVisibility(View.GONE);
            holder.status_text_view2.setVisibility(View.VISIBLE);
        }
        holder.attendanceCodeTextView.setText(attendance.getCode());

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Attendance attendance = attendanceList.get(position);
                String studentSnapshotKey = attendance.getName(); // Assuming name is the student snapshot key
                String attendanceId = attendance.getName();
                String course=attendance.getCourse();
                String selectedBatchName=attendance.getSelectedBatchName();
                String selectedBatchSubjectName=attendance.getSelectedBatchSubjectName();
                String currentDate=attendance.getCurrentDate();
                activity.InvalidMarkStudentAttendance(course, selectedBatchName, selectedBatchSubjectName,currentDate,attendanceId);
            }
        });

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Attendance attendance = attendanceList.get(position);
                String attendanceId = attendance.getName();
                String course=attendance.getCourse();
                String selectedBatchName=attendance.getSelectedBatchName();
                String selectedBatchSubjectName=attendance.getSelectedBatchSubjectName();
                String currentDate=attendance.getCurrentDate();
                activity.ValidMarkStudentAttendance(course, selectedBatchName,
                        selectedBatchSubjectName,currentDate,attendanceId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView rollNoTextView;
        TextView nameTextView;
        TextView attendanceCodeTextView;
        TextView status_text_view1,status_text_view2;
        ImageView removeBtn,addBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find view elements by their IDs in your layout
            removeBtn=itemView.findViewById(R.id.removeBtn);
            addBtn=itemView.findViewById(R.id.addBtn);
            rollNoTextView = itemView.findViewById(R.id.roll_no_text_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            status_text_view1=itemView.findViewById(R.id.status_text_view1);
            status_text_view2=itemView.findViewById(R.id.status_text_view2);
            attendanceCodeTextView = itemView.findViewById(R.id.attendance_code_text_view);
        }
    }

}
