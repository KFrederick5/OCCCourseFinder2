package edu.orangecoastcollege.cs273.occcoursefinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class CourseSearchActivity extends AppCompatActivity {

    private DBHelper db;
    private List<Instructor> allInstructorsList;
    private List<Course> allCoursesList;
    private List<Offering> allOfferingsList;
    private List<Offering> filteredOfferingsList;

    private EditText courseTitleEditText;
    private Spinner instructorSpinner;
    private ListView offeringsListView;

    private OfferingListAdapter offeringListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);

        deleteDatabase(DBHelper.DATABASE_NAME);
        db = new DBHelper(this);
        db.importCoursesFromCSV("courses.csv");
        db.importInstructorsFromCSV("instructors.csv");
        db.importOfferingsFromCSV("offerings.csv");

        allOfferingsList = db.getAllOfferings();
        filteredOfferingsList = new ArrayList<>(allOfferingsList);
        allInstructorsList = db.getAllInstructors();
        allCoursesList = db.getAllCourses();

        courseTitleEditText = (EditText) findViewById(R.id.courseTitleEditText);
        courseTitleEditText.addTextChangedListener(courseTitleTextWatcher);

        instructorSpinner = (Spinner) findViewById(R.id.instructorSpinner);
        ArrayAdapter<String> instructorSpinnerAdapter=
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                        getInstructorNames());
        instructorSpinner.setAdapter(instructorSpinnerAdapter);
        instructorSpinner.setOnItemSelectedListener(spinnerListener);


        offeringsListView = (ListView) findViewById(R.id.offeringsListView);
        offeringListAdapter =
                new OfferingListAdapter(this, R.layout.offering_list_item, filteredOfferingsList);
        offeringsListView.setAdapter(offeringListAdapter);







        /*
       Cursor instructorNamesCursor = db.getInstructorNamesCursor();
        SimpleCursorAdapter cursorAdapter =
                new SimpleCursorAdapter(this,
                        android.R.layout.simple_spinner_item,
                        instructorNamesCursor,
                        new String[] {DBHelper.FIELD_LAST_NAME},
                        new int[] {android.R.id.text1}, 0);
        cursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instructorSpinner.setAdapter(cursorAdapter);
        */
    }

    private String[] getInstructorNames()
    {
        String[] instructorNames= new String[allInstructorsList.size() + 1];
        instructorNames[0] = getString(R.string.select_instructor);
        for (int i = 1; i < instructorNames.length; i++)
        {
            instructorNames[i] = allInstructorsList.get(i - 1).getFullName();
        }
        return instructorNames;
    }

    public TextWatcher courseTitleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            String input = s.toString().toLowerCase();

            if(input.equals(""))
            {
                offeringListAdapter.clear();
                for(Offering offering : allOfferingsList)
                    offeringListAdapter.add(offering);
            }
            else
            {
                offeringListAdapter.clear();
                for(Offering offering : allOfferingsList)
                {
                    Course course = offering.getCourse();
                    if (course.getTitle().toLowerCase().contains(input))
                        offeringListAdapter.add(offering);
                }
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public AdapterView.OnItemSelectedListener spinnerListener =
            new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int i, long l) {
            String selectedInstructor = String.valueOf(parent.getItemAtPosition(i));
            offeringListAdapter.clear();
            if (selectedInstructor.equals(getString(R.string.select_instructor)))
            {
                for (Offering offering : allOfferingsList)
                    offeringListAdapter.add(offering);
            }
            else
            {
                for (Offering offering : allOfferingsList)
                {
                    Instructor instructor = offering.getInstructor();
                    if(instructor.getFullName().equals(selectedInstructor))
                        offeringListAdapter.add(offering);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            parent.setSelection(0);
        }
    };

    public void reset(View v)
    {
        courseTitleEditText.setText("");
        instructorSpinner.setSelection(0);
    }


}
