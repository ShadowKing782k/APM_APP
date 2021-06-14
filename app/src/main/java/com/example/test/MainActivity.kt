package com.example.test

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.database.*
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries


class MainActivity : AppCompatActivity() {

    lateinit private var firebasedatabase: FirebaseDatabase
    lateinit private var databaseReference: DatabaseReference
    lateinit private var dbref : DatabaseReference
    lateinit private var H2ref : DatabaseReference
    lateinit private var LPGref : DatabaseReference
    lateinit private var COref : DatabaseReference
    lateinit private var NH3ref : DatabaseReference
    lateinit private var Touleneref : DatabaseReference
    lateinit private var CO2ref : DatabaseReference
   // private var channelId : String = "12345"


    fun notification(notificationManager: NotificationManager, description: String, ns: String, channelId : String, Notif_ID : Int)
    {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println("lol")
           val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

           val notificationChannel = NotificationChannel(
                   channelId,
                   description,
                   NotificationManager.IMPORTANCE_HIGH
           )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            val builder = NotificationCompat.Builder(this, channelId)
                .setContentText(ns)
                .setContentTitle("ALERT!!!")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(
                        BitmapFactory.decodeResource(
                                this.resources,
                                R.drawable.ic_launcher_background
                        )
                )
                .setAutoCancel(true)



            notificationManager.notify(Notif_ID, builder.build())
        }

        else{
            val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)

            notificationManager.notify(1234, builder.build())
        }


    }

    fun call(snapshot: DataSnapshot, Threshold: Float, series: LineGraphSeries<DataPoint>, Tseries: LineGraphSeries<DataPoint>, param : String, channelId : String, Notif_ID: Int)
    {
        val value = snapshot.getValue(String::class.java)?.split(",")
        if(value != null)
        {
            if(value[1].toDouble() > Threshold)
            {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val description = "Warning!!!!"
                notification(notificationManager, description, param + "Exceeded!!!", channelId, Notif_ID)
            }
            //println(value)
            series.appendData(DataPoint(value[0].toDouble(), value[1].toDouble()), true, 50)
            Tseries.appendData(DataPoint(value[0].toDouble(), Threshold.toDouble()), true, 50)
        }


    }

    fun graphConfig(graph: GraphView, title: String)
    {
        graph.title = title
        val viewport :Viewport = graph.viewport
        viewport.setScalable(true)
        viewport.setScrollable(true)
        viewport.setScalableY(true)
        viewport.setScrollableY(true)
        viewport.setMinX(0.0)
        viewport.setMaxX(10.0)

        val gridLabel: GridLabelRenderer = graph.gridLabelRenderer
        gridLabel.horizontalAxisTitle = "Time"
        gridLabel.gridColor = Color.WHITE
        gridLabel.horizontalAxisTitleColor = Color.WHITE
        gridLabel.horizontalLabelsColor = Color.WHITE
        gridLabel.verticalLabelsColor = Color.WHITE
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(baseContext, MyService::class.java))
        setTitle("APM") //Sets the action of 
        firebasedatabase = FirebaseDatabase.getInstance();
        //firebasedatabase.setPersistenceEnabled(true)
        databaseReference = firebasedatabase.getReference("temp")
        dbref = firebasedatabase.getReference("humidity")
        H2ref = firebasedatabase.getReference("H2")
        LPGref = firebasedatabase.getReference("LPG")
        COref = firebasedatabase.getReference("CO")
        NH3ref = firebasedatabase.getReference("NH3")
        Touleneref = firebasedatabase.getReference("TOULENE")
        CO2ref = firebasedatabase.getReference("CO2")
        val temp_graph : GraphView = findViewById(R.id.Temp)
        val humid_graph : GraphView = findViewById(R.id.Humid)
        val h2_graph : GraphView = findViewById(R.id.H2)
        val lpg_graph : GraphView = findViewById(R.id.LPG)
        val co_graph : GraphView = findViewById(R.id.CO)
        val nh3_graph : GraphView = findViewById(R.id.NH3)
        val Toulene_graph : GraphView = findViewById(R.id.TOULENE)
        val co2_graph : GraphView = findViewById(R.id.CO2)
        temp_graph.setBackgroundColor(getResources().getColor(R.color.Grey))
        humid_graph.setBackgroundColor(getResources().getColor(R.color.Grey))
        h2_graph.setBackgroundColor(getResources().getColor(R.color.Grey))
        lpg_graph.setBackgroundColor(getResources().getColor(R.color.Grey))
        co_graph.setBackgroundColor(getResources().getColor(R.color.Grey))
        nh3_graph.setBackgroundColor(getResources().getColor(R.color.Grey))
        Toulene_graph.setBackgroundColor(getResources().getColor(R.color.Grey))
        co2_graph.setBackgroundColor(getResources().getColor(R.color.Grey))
        temp_graph.titleColor = Color.WHITE
        humid_graph.titleColor = Color.WHITE
        h2_graph.titleColor = Color.WHITE
        lpg_graph.titleColor = Color.WHITE
        co_graph.titleColor = Color.WHITE
        nh3_graph.titleColor = Color.WHITE
        Toulene_graph.titleColor = Color.WHITE
        co2_graph.titleColor = Color.WHITE
        //Basic Config of Graph

        graphConfig(temp_graph, "TEMPERATURE")
        graphConfig(humid_graph, "HUMIDITY")
        graphConfig(h2_graph, "HYDROGEN")
        graphConfig(lpg_graph, "LPG")
        graphConfig(co_graph, "CARBON-MONOXIDE")
        graphConfig(nh3_graph, "NH3")
        graphConfig(Toulene_graph, "TOULENE")
        graphConfig(co2_graph, "CARBON-DIOXIDE")

         val series = LineGraphSeries(arrayOf<DataPoint>())
        series.setColor(getResources().getColor(R.color.Dark_Pink))
         val series2 = LineGraphSeries(arrayOf<DataPoint>())
        series2.setColor(getResources().getColor(R.color.Dark_Pink))
        val series3 = LineGraphSeries(arrayOf<DataPoint>())
        series3.setColor(getResources().getColor(R.color.Dark_Pink))
        val series4 = LineGraphSeries(arrayOf<DataPoint>())
        series4.setColor(getResources().getColor(R.color.Dark_Pink))
        val series5 = LineGraphSeries(arrayOf<DataPoint>())
        series5.setColor(getResources().getColor(R.color.Dark_Pink))
        val series6 = LineGraphSeries(arrayOf<DataPoint>())
        series6.setColor(getResources().getColor(R.color.Dark_Pink))
        val series7 = LineGraphSeries(arrayOf<DataPoint>())
        series7.setColor(getResources().getColor(R.color.Dark_Pink))
        val series8 = LineGraphSeries(arrayOf<DataPoint>())
        series8.setColor(getResources().getColor(R.color.Dark_Pink))
        val series9 = LineGraphSeries(arrayOf<DataPoint>())
        series9.setColor(getResources().getColor(R.color.Threshold_color))
        val series10 = LineGraphSeries(arrayOf<DataPoint>())
        series10.setColor(getResources().getColor(R.color.Threshold_color))
        val series11 = LineGraphSeries(arrayOf<DataPoint>())
        series11.setColor(getResources().getColor(R.color.Threshold_color))
        val series12 = LineGraphSeries(arrayOf<DataPoint>())
        series12.setColor(getResources().getColor(R.color.Threshold_color))
        val series13 = LineGraphSeries(arrayOf<DataPoint>())
        series13.setColor(getResources().getColor(R.color.Threshold_color))
        val series14 = LineGraphSeries(arrayOf<DataPoint>())
        series14.setColor(getResources().getColor(R.color.Threshold_color))
        val series15 = LineGraphSeries(arrayOf<DataPoint>())
        series15.setColor(getResources().getColor(R.color.Threshold_color))
        val series16 = LineGraphSeries(arrayOf<DataPoint>())
        series16.setColor(getResources().getColor(R.color.Threshold_color))

         temp_graph.addSeries(series)
         temp_graph.addSeries(series15)
          humid_graph.addSeries(series2)
         humid_graph.addSeries(series16)
         h2_graph.addSeries(series3)
        h2_graph.addSeries(series9)
        lpg_graph.addSeries(series4)
        lpg_graph.addSeries(series10)
        co_graph.addSeries(series5)
        co_graph.addSeries(series11)
        nh3_graph.addSeries(series6)
        nh3_graph.addSeries(series12)
        Toulene_graph.addSeries(series7)
        Toulene_graph.addSeries(series13)
         co2_graph.addSeries(series8)
         co2_graph.addSeries(series14)

            //notification(notificationManager, description)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                call(snapshot, 40.toFloat(), series, series15, "Temperature", "12345", 1)

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                call(snapshot, 70.toFloat(), series2, series16, "Humidity", "12346", 2)
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        H2ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                call(snapshot, 10.toFloat(), series3, series9, "H2 Conc.", "12352", 3)
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        LPGref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                call(snapshot, 2000.toFloat(), series4, series10, "LPG Conc.", "12347", 4)
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        COref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                call(snapshot, 50.toFloat(), series5, series11, "CO Conc.", "12348", 5)

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        NH3ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                call(snapshot, 25.toFloat(), series6, series12, "NH3 Conc.", "12349", 6)
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        Touleneref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                call(snapshot, 100.toFloat(), series7, series13, "Toluene Conc.","12350", 7)

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

        CO2ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                call(snapshot, 5000.toFloat(), series8, series14, "CO2 Conc.", "12351", 8)

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

    }


        }



