package com.kashish.product.milkmanager.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.applandeo.materialcalendarview.EventDay
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.material.navigation.NavigationView
import com.kashish.product.milkmanager.R
import com.kashish.product.milkmanager.entity.Record
import com.kashish.product.milkmanager.helper.AppConstants
import com.kashish.product.milkmanager.helper.PreferenceHelper
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomePageActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var mDrawerToggle: ActionBarDrawerToggle
    var mToolBarNavigationListenerIsRegistered = false
    lateinit var compactCalendar: CompactCalendarView
    private val currentCalender = Calendar.getInstance(Locale.getDefault())
    private val dateFormatForDisplaying = SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault())
    private val dateFormatForMonth = SimpleDateFormat("MMMM - yyyy", Locale.getDefault())
    lateinit var record: Record
    lateinit var format: DateFormat
    lateinit var defaultDateFormatString: String
    lateinit var defaultDateFormat: SimpleDateFormat
    lateinit var preferenceHelper: PreferenceHelper
    lateinit var todayDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        format = android.text.format.DateFormat.getDateFormat(applicationContext)
        defaultDateFormatString = (format as SimpleDateFormat).toLocalizedPattern()
        defaultDateFormat = SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy", Locale.getDefault())
        preferenceHelper = PreferenceHelper(this@HomePageActivity)


        record = Record(false, 0.0f, "", 0.0F, currentCalender.time)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        //supportActionBar!!.setIcon(R.drawable.logo)
        supportActionBar!!.title = ""

        val navBar = findViewById<View>(R.id.nav_view) as NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        mDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
        enableViews(drawerLayout.isDrawerOpen(GravityCompat.START))

        compactCalendar = findViewById(R.id.compactcalendar_view)

        val events = ArrayList<EventDay>()
        val calendar = Calendar.getInstance()
        events.add(EventDay(calendar, R.drawable.home))
        calendar.set(2019, 8, 15)
        events.add(EventDay(calendar, R.color.colorAccent))


        val list = mutableListOf<Calendar>()
        list.add(calendar)

        compactCalendar.shouldDrawIndicatorsBelowSelectedDays(true)

        todayDate = Calendar.getInstance().time
        val df = defaultDateFormat.format(todayDate)
        todayDate = defaultDateFormat.parse(df)

//        compactCalendar.addEvent(Event(R.color.red,1564053664000))
//        compactCalendar.addEvent(Event(R.color.green,1564140064000))
//        compactCalendar.addEvent(Event(Color.argb(255, 50, 175, 50), 1564053664000))
//        compactCalendar.addEvent(Event(Color.argb(255, 255, 0, 0), 1564140064000))
//        compactCalendar.addEvent(Event(R.drawable.left, 1564053664000))
//        compactCalendar.addEvent(Event(Color.argb(255, 50, 175, 50), 1564053664000))
//
        showEventsForMonth(todayDate.month, todayDate.year)



        compactCalendar.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                val NEwRecord = showStatus(dateClicked)
                if (NEwRecord.rate != 0.0F) {

                } else {
                    addStatus(dateClicked)
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
//                Log.d(FragmentActivity.TAG, "Month was scrolled to: $firstDayOfNewMonth")
                month_year.text = dateFormatForMonth.format(firstDayOfNewMonth)
            }
        })

        compactCalendar.setUseThreeLetterAbbreviation(true)

        left.setOnClickListener {
            compactCalendar.scrollLeft()
        }

        right.setOnClickListener {
            compactCalendar.scrollRight()
        }

        compactCalendar.shouldSelectFirstDayOfMonthOnScroll(false)


    }

    private fun addStatus(date: Date) {

        record = Record(false, preferenceHelper.getQuantity(), "", preferenceHelper.getRate(), date)
        record.date = date
        record.rate = preferenceHelper.getRate()
        record.quantity = preferenceHelper.getQuantity()
        record.notes = ""
        record.tookMilk = false
        lateinit var set: HashSet<String>
//        set = preferenceHelper.addItemInSet(record)

        yes_took_milk.setOnClickListener {
            record.tookMilk = true
            set = preferenceHelper.addItemInSet(record)
            preferenceHelper.saveStringSet(AppConstants().RECORDS, set)
            quantityCV.visibility=View.VISIBLE
//            startActivity(Intent(this@HomePageActivity,HomePageActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
//            finish()
            showEventsForMonth(date.month,date.year)
        }

        no_took_milk.setOnClickListener {
            record.tookMilk = false
            record.quantity = 0.0F
            set = preferenceHelper.addItemInSet(record)
            preferenceHelper.saveStringSet(AppConstants().RECORDS, set)
            quantityCV.visibility=View.GONE
//            finish()
            showEventsForMonth(date.month,date.year)
        }
        notes_editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                record.notes = s.toString()
                preferenceHelper.removeItem(AppConstants().RECORDS, set.toString())
                set = preferenceHelper.addItemInSet(record)
                preferenceHelper.saveStringSet(AppConstants().RECORDS, set)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        quantity_plus.setOnClickListener {
            set = preferenceHelper.addItemInSet(record)
            preferenceHelper.removeItem(AppConstants().RECORDS, set.toString())
            record.quantity = record.quantity + 0.5F
            set = preferenceHelper.addItemInSet(record)
            preferenceHelper.saveStringSet(AppConstants().RECORDS, set)
        }
        quantity_minus.setOnClickListener {
            if (record.quantity > 0.0F) {
                set = preferenceHelper.addItemInSet(record)
                preferenceHelper.removeItem(AppConstants().RECORDS, set.toString())
                record.quantity = record.quantity - 0.5F
                set = preferenceHelper.addItemInSet(record)
                preferenceHelper.saveStringSet(AppConstants().RECORDS, set)
            }
        }
    }

    private fun showStatus(date: Date): Record {
        val myList = mutableListOf<Record>()
        val newRecord=Record(false,0.0F,"",0.0F,date)
        val records = PreferenceHelper(this).getStringSet(AppConstants().RECORDS)
        if (records != null) {
            for (s in records) {
                try {
                    val jsonObject = JSONObject(s)
                    val cartItem = Record(
                        jsonObject.getBoolean("TookMilk"),
                        jsonObject.getDouble("Quantity").toFloat(),
                        jsonObject.getString("Notes"),
                        jsonObject.getDouble("Rate").toFloat(),
                        defaultDateFormat.parse(jsonObject.getString("Date"))
                    )
                    myList.add(cartItem)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        myList.forEach {
            if (it.date == date) {
                showEvent(it)
                return it
            }
        }
        return newRecord
    }

    private fun showEvent(record: Record) {

    }

    private fun showEventsForMonth(month: Int, year: Int) {
        currentCalender.time = Calendar.getInstance().time
        currentCalender.set(Calendar.DAY_OF_MONTH, month)
        val dirstDayOfMonth = currentCalender.time

        val myList = mutableListOf<Record>()
        val records = PreferenceHelper(this).getStringSet(AppConstants().RECORDS)
        if (records != null) {
            for (s in records) {
                try {
                    val jsonObject = JSONObject(s)
                    val cartItem = Record(
                        jsonObject.getBoolean("TookMilk"),
                        jsonObject.getDouble("Quantity").toFloat(),
                        jsonObject.getString("Notes"),
                        jsonObject.getDouble("Rate").toFloat(),
                        defaultDateFormat.parse(jsonObject.getString("Date"))
                    )
                    myList.add(cartItem)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            val monthList = mutableListOf<Event>()

            myList.forEach {
                if (it.date.year == year && it.date.month == month) {
                    currentCalender.set(Calendar.YEAR, year+1900)
                    currentCalender.set(Calendar.MONTH, month)
                    currentCalender.set(Calendar.ERA, GregorianCalendar.AD)
                    currentCalender.set(Calendar.DATE, it.date.date)
                    Log.e("date",currentCalender.time.toString())
                    compactCalendar.removeAllEvents()
//                    var newEvent=Event(Color.argb(255, 50, 175, 50), currentCalender.timeInMillis)
                    if (it.tookMilk) {
//                        newEvent=
//                        compactCalendar.addEvent(Event(Color.argb(255, 50, 175, 50), currentCalender.timeInMillis))
                        monthList.add(Event(Color.argb(255, 50, 175, 50), currentCalender.timeInMillis))
                    } else {
//                        newEvent=
//                        compactCalendar.addEvent(Event(Color.argb(255, 255, 50, 50), currentCalender.timeInMillis))
                        monthList.add(Event(Color.argb(255, 255, 50, 50), currentCalender.timeInMillis))
                    }
//                    val compactCalendar.getEventsForMonth(it.date)
                    compactCalendar.removeAllEvents()
                }
            }
            compactCalendar.addEvents(monthList)
        }
    }


    private fun addEvents(month: Int, year: Int) {
        currentCalender.time = Date()
        currentCalender.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = currentCalender.time
        for (i in 0..5) {
            currentCalender.time = firstDayOfMonth
            if (month > -1) {
                currentCalender.set(Calendar.MONTH, month)
            }
            if (year > -1) {
                currentCalender.set(Calendar.ERA, GregorianCalendar.AD)
                currentCalender.set(Calendar.YEAR, year)
            }
            currentCalender.add(Calendar.DATE, i)
            setToMidnight(currentCalender)
            val timeInMillis = currentCalender.timeInMillis

            val events = getEvents(timeInMillis, i)

            compactCalendar.addEvents(events)
        }
    }

    private fun getEvents(timeInMillis: Long, day: Int): List<Event> {
        return if (day < 2) {
            Arrays.asList(Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + Date(timeInMillis)))
        } else if (day > 2 && day <= 4) {
            Arrays.asList(
                Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + Date(timeInMillis)),
                Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + Date(timeInMillis))
            )
        } else {
            Arrays.asList(
                Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + Date(timeInMillis)),
                Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + Date(timeInMillis)),
                Event(Color.argb(255, 70, 68, 65), timeInMillis, "Event 3 at " + Date(timeInMillis))
            )
        }
    }

    private fun setToMidnight(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun enableViews(enable: Boolean) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (enable) {
            //You may not want to open the drawer on swipe from the left in this case
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            // Remove hamburger
            mDrawerToggle.isDrawerIndicatorEnabled = false
            // Show back button
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                mDrawerToggle.toolbarNavigationClickListener = object : View.OnClickListener {
                    override fun onClick(v: View) {
                        // Doesn't have to be onBackPressed
                        onBackPressed()
                    }
                }

                mToolBarNavigationListenerIsRegistered = true
            }

        } else {
            //You must regain the power of swipe for the drawer.
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

            // Remove back button
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            // Show hamburger
            mDrawerToggle.isDrawerIndicatorEnabled = true
            // Remove the/any drawer toggle listener
            mDrawerToggle.toolbarNavigationClickListener = null
            mToolBarNavigationListenerIsRegistered = false
        }

        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }
}