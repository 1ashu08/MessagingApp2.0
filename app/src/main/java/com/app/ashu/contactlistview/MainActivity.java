package com.app.ashu.contactlistview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ashu.contactlistview.utility.ContactListAdapter;
import com.app.ashu.contactlistview.utility.CreateContactList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }*/

    ListView inActiveMember,activeMember ;
    //RecyclerView activeMember;
    ArrayList<String> activeContacts,inActiveContacts ;
    ArrayAdapter<String> inActiveContactAdapter,activeContactAdapter;
    //ContactListAdapter activeContactAdapter;

    Cursor cursor ;
    String name, phonenumber ;
    public  static final int RequestPermissionCode  = 1 ;
    Button button;
    TextView txtView;
    EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //activeMember = (RecyclerView)findViewById(R.id.activeMember);
        inActiveMember=(ListView)findViewById(R.id.inActiveMember);
        activeMember=(ListView)findViewById(R.id.activeMember);
        searchBox=(EditText)findViewById(R.id.searchBox);


        button = (Button)findViewById(R.id.button1);



        activeContacts = new ArrayList<String>();
        inActiveContacts=new ArrayList<String>();

        EnableRuntimePermission();

        final CreateContactList createList=new CreateContactList();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String,ArrayList<String>> list=createList.getContactList(MainActivity.this);
                activeContacts=list.get("Active");
                permanentActieList=createList.getContactList(MainActivity.this).get("Active");
                inActiveContacts=list.get("InActive");
                //GetContactsIntoArrayList();
                //new ArrayAdapter<String>();
                /*txtView=(TextView)findViewById(R.id.textView);
                txtView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        System.out.println("In the text View");
                    }
                });*/

                //activeContactAdapter = new ContactListAdapter(activeContacts);
                activeContactAdapter = new ArrayAdapter<String>(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1,activeContacts
                );

                inActiveContactAdapter = new ArrayAdapter<String>(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1,inActiveContacts
                );

                activeMember.setAdapter(activeContactAdapter);
                inActiveMember.setAdapter(inActiveContactAdapter);

                setDynamicHeight(activeMember);
                setDynamicHeight(inActiveMember);

                /*activeMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("In the text View ");

                        GotoMessage(v.getId());
                    }
                });*/
                activeMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println("In the text View "+position+"  "+id+" "+view.toString());

                        GotoMessage(id);
                    }

                });

            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //System.out.println("Before Chage");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text=searchBox.getText().toString();

                /*if(text!=null)
                {
                    prepareSearchedList(text);
                }
                else
                {
                    activeContacts=permanentActieList;
                    activeContactAdapter.notifyDataSetChanged();
                }*/
                //System.out.println("On Chage");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(activeContactAdapter!=null)
                {
                    String text=searchBox.getText().toString();

                    if(text!=null && text.length()>0)
                    {
                        System.out.println("In if ");
                        prepareSearchedList(text);
                        setDynamicHeight(activeMember);
                    }
                    else
                    {
                        //System.out.println("In else block");
                        activeContacts.clear();
                        System.out.println(permanentActieList.size());
                        for(int i=0;i<permanentActieList.size();i++)
                            activeContacts.add(permanentActieList.get(i));
                        activeContactAdapter.notifyDataSetChanged();
                        setDynamicHeight(activeMember);
                    }
                }

                //System.out.println("After Chage");
            }
        });


    }
    ArrayList<String> permanentActieList=new ArrayList<String>();
    public void prepareSearchedList(String text)
    {
        ArrayList<String> newList=new ArrayList<String>();
        for(String data:permanentActieList)
        {
            text= text.toLowerCase();
            data=data.toLowerCase();
            if(data.contains(text))
            {
                newList.add(data);
            }
        }
        System.out.println("Size is "+newList.size());
        activeContacts.clear();
        for(int i=0;i<newList.size();i++)
            activeContacts.add(newList.get(i));
        activeContactAdapter.notifyDataSetChanged();
    }


    public static void setDynamicHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        //check adapter if null
        if (adapter == null) {
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
    }


    public void GotoMessage(long id)
    {
        int i=(int)id;
        SpecificUserMessage.dName=activeContacts.get(i).split(":")[0];

        Intent msg=new Intent(this,SpecificUserMessage.class);
        this.startActivity(msg);

        //finish();

    }

    /*public void GetContactsIntoArrayList(){

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            StoreContacts.add(name + " "  + ":" + " " + phonenumber);
        }

        cursor.close();

    }*/

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this,
                Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(MainActivity.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }


}


