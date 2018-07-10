package com.example.q.project3ver3

import android.support.design.widget.TabLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.q.project3ver3.model.Contact
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

//var contact_list = ArrayList<Contact>()

class MainActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    lateinit var userId : String
    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Login Acitiviy에서 보내준 intent를 받는다
        val intent = getIntent()
        userId = intent.getStringExtra("userId")

        //DB에서 contact_list의 필요한 값들을 load한다
//        loadData(userId)

        //contact 임의의 값
//        val tmp = Contact("전형준","010-4830-0139","path")
//        contact_list.add(tmp)
//        contact_list.add(tmp)


        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById<View>(R.id.container) as ViewPager
        mViewPager!!.adapter = mSectionsPagerAdapter

        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout

        mViewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(mViewPager))

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    fun loadData(userId: String) {
        val data = HashMap<String, String>()

        data.put("userId", userId)
        data.put("tag", "load")

        postDataForLoad(LoginActivity().url, data)
    }

    fun postDataForLoad(url: String, data: HashMap<String, String>) {
//        var requstQueue = Volley.newRequestQueue(this)

        val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, JSONObject(data),
                Response.Listener { response ->
                    var friend_list = response.getJSONArray("friendList")
                    jsonArrayParsing(friend_list)

                    val contact_adapter = MyContact.contactAdapter(contact_list, MyContact().context)
                    MyContact().recyclerView.setAdapter(contact_adapter)

                },
                Response.ErrorListener { error ->1
                    error.toString()
                    print("error")
                }
        ) {

            //here I want to post data to sever
        }
//        requstQueue.add(jsonobj)
        MySingleton.getInstance(this).addToRequestQueue(jsonRequest)
    }

    fun jsonArrayParsing(json_array : JSONArray){
        for(i in 0 until json_array.length()-1 ){
            var json_obj = json_array.getJSONObject(i)
            var name  = json_obj.getString("name")
            var phone_number = json_obj.getString("phoneNumber")
            var profile = json_obj.getString("profile")

            var contact = Contact(name, phone_number, profile)
            contact_list.add(contact)

        }
    }

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            val textView = rootView.findViewById<View>(R.id.section_label) as TextView
            textView.text = getString(R.string.section_format, arguments!!.getInt(ARG_SECTION_NUMBER))
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return when(position){
                0 -> MyContact()
                1 -> Gallery()
                else -> {
                    return Anonymous()
                }
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }
}
