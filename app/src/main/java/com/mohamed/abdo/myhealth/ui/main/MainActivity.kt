package com.mohamed.abdo.myhealth.ui.main

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mohamed.abdo.myhealth.R
import com.mohamed.abdo.myhealth.databinding.ActivityMainBinding
import com.mohamed.abdo.myhealth.databinding.CustomLayoutBinding
import com.mohamed.abdo.myhealth.pojo.AlarmReceiver
import com.mohamed.abdo.myhealth.ui.about.AboutActivity
import com.mohamed.abdo.myhealth.ui.chat.ChatActivity
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
private const val RESULT_SELECTED_IMAGE = 36
class MainActivity : AppCompatActivity() , OnClickConfirm {
    lateinit var binding : ActivityMainBinding
    lateinit var myAdapter: MainAdapter
    // dialogBinding
    lateinit var dialogBinding : CustomLayoutBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    val myList: MutableList<String> = ArrayList()
    lateinit var dialog :Dialog
    //var location
   private var PermissionID = 99
   lateinit var fusedLocationProviderClient: FusedLocationProviderClient
   lateinit var locationRequest: LocationRequest
   private lateinit var locationFlag : String
   var countryName:String = ""
   var cityName:String =""
   // var get Image from user
    private lateinit var userImageProfile : String

    val randomString = UUID.randomUUID().toString().substring(0,15)

    private val firebaseStore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storageInstance : FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val currentUSerStorageRef : StorageReference
        get() =  storageInstance.reference.child(randomString)

    // drawer var
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dialogBinding = CustomLayoutBinding.inflate(layoutInflater)
        createNotificationChannel()
        setAlarmManager()
        //bottom navigation bar
        drawerNavigationBar()
        FirebaseApp.initializeApp(this)
        initRecyclerView()
        initButton()
        // init fusedLocationProviderClient 5
        initFusedLocationProviderClient()
        myAdapter = MainAdapter()
        binding.RecyclerViewQuestion.adapter = myAdapter
        myAdapter.setOnItemClickListener(this@MainActivity)

    }


    private fun setAlarmManager()
    {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this , AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
            AlarmManager.INTERVAL_HOUR,pendingIntent
        )
        Log.d("alarmManager",alarmManager.toString())
    }

    private fun createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name :CharSequence = "Corona Virus"
            val description = "Stay save"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("mohamed",name,importance)
            channel.description = description
            val notificationManger = getSystemService(NotificationManager::class.java)
            notificationManger.createNotificationChannel(channel)
            Log.d("notificationManger",notificationManger.toString())

        }
    }

    private fun drawerNavigationBar()
    {

        toggle = ActionBarDrawerToggle(
            this@MainActivity,
            binding.drawer,
            binding.toolbarHome,
            R.string.open,
            R.string.close)
        binding.drawer.addDrawerListener(toggle)
        setSupportActionBar(binding.toolbarHome)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_drawer);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener {item->
            when(item.itemId){
                R.id.menu_home ->{
                    val intent = Intent(this@MainActivity,MainActivity::class.java)
                    startActivity(intent)
                    this@MainActivity.finish()
                    true
                }
                R.id.menu_about ->{
                    val intent = Intent(this@MainActivity,AboutActivity::class.java)
                    startActivity(intent)
                    this@MainActivity.finish()
                    true
                }
                R.id.menu_chat ->{
                    val intent = Intent(this@MainActivity, ChatActivity::class.java)
                    startActivity(intent)
                    true
                }
                else ->{
                    false
                }
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (toggle.onOptionsItemSelected(item))
        {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initButton()
    {
        binding.btnConfirm.setOnClickListener {
                if (myList.isNotEmpty())
                {
                    if (myList[0] == "yes" && myList[1] == "yes" && myList[2] == "yes" )
                    {
                        showDialog( "You Suspect Corona")
                    }
                    else
                    {
                        showDialog("You don't suspect Corona")
                    }
                }

            }
        dialogBinding.ivUploadPhoto.setOnClickListener {
            val myIntentImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
            }
            startActivityForResult(Intent.createChooser(myIntentImage,"select Image"),
                RESULT_SELECTED_IMAGE)


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_SELECTED_IMAGE && resultCode == Activity.RESULT_OK
            && data !=null && data.data !=null)
        {
            dialogBinding.progressBarProfile.visibility = View.VISIBLE
            dialogBinding.ivUploadPhoto.setImageURI(data.data)
            // Reducing the image size by compress
            val selectedImagePath = data.data
            val selectedImageBitMap = MediaStore.Images.Media.getBitmap(this.contentResolver
                ,selectedImagePath)
            val outPutStream = ByteArrayOutputStream()
            selectedImageBitMap.compress(Bitmap.CompressFormat.JPEG , 20,outPutStream)
            val selectedImageBytes = outPutStream.toByteArray()
            uploadImageToDataBase(selectedImageBytes){ path ->
                    userImageProfile = path
                   dialogBinding.btnConfirmDialog.setOnClickListener {
                       val map = mutableMapOf<String , Any>()
                       map["imageProfile"] = userImageProfile
                       map["CountryName"] = countryName
                       map["cityName"] = cityName
                       dialogBinding.progressBarProfile.visibility = View.VISIBLE
                       firebaseStore.collection("Users")
                           .add(map)
                           .addOnSuccessListener {
                               dialogBinding.btnConfirmDialog.setBackgroundColor(Color.TRANSPARENT)
                               dialogBinding.btnConfirmDialog.isEnabled = false
                               dialogBinding.tvMustUploadPhoto.text="Upload Photo Success"
                               dialogBinding.progressBarProfile.visibility = View.GONE
                               val intent = Intent(this@MainActivity, AboutActivity::class.java)
                               startActivity(intent)
                               this@MainActivity.finish()
                               Toast.makeText(this,"Data Send Success"
                                   ,Toast.LENGTH_SHORT).show()
                           }
                           .addOnFailureListener{ e->
                               dialogBinding.progressBarProfile.visibility = View.GONE
                               Toast.makeText(this,"Error " + e.message
                                   ,Toast.LENGTH_SHORT).show()

                           }
                   }
            }
        }
    }

    private fun uploadImageToDataBase(selectedImageBytes: ByteArray
                                      , onSuccess : (imagePath : String)->Unit)
    {
        val ref = currentUSerStorageRef
            .child("ProfileImage/${UUID.nameUUIDFromBytes(selectedImageBytes)}")
        ref.putBytes(selectedImageBytes).addOnCompleteListener(OnCompleteListener { task->
            if (task.isSuccessful)
            {
                onSuccess(ref.path)
                dialogBinding.progressBarProfile.visibility = View.GONE
            }
            else{
                Toast.makeText(this@MainActivity,"Error " +
                        task.exception?.message.toString()
                    ,Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initRecyclerView()
    {
        linearLayoutManager = LinearLayoutManager(this)
        binding.RecyclerViewQuestion.layoutManager = linearLayoutManager

    }

    override fun onClick(answer: String, pos: Int)
    {

        if(pos == 0 && answer == "yes")
        {
            myList.add("yes")
        }
        if(pos == 1 && answer == "yes")
        {
            myList.add("yes")
        }
        if(pos == 2 && answer == "yes")
        {
            myList.add("yes")
        }
        if(pos == 0 && answer == "no")
        {
            myList.add("no")

        }
        if(pos == 1 && answer == "no"){
            myList.add("no")
        }
        if(pos == 2 && answer == "no")
        {
            myList.add("no")
        }

        }

    private fun showDialog(maySuspect:String)
    {
         dialog = Dialog(this,R.style.AlertDialogTheme)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvLocation.setOnClickListener { v->
            getLastLocation()
        }
        dialogBinding.tvCloseDialog.setOnClickListener { v->
            myList.clear()
            dialog.dismiss()
        }
        dialogBinding.tvCoronaSuspect.text = maySuspect
        if (maySuspect == "You don't suspect Corona")
        {
            dialogBinding.tvLocation.visibility = View.GONE
            dialogBinding.tvMustUploadPhoto.visibility = View.GONE
            dialogBinding.ivUploadPhoto.visibility = View.GONE
            dialogBinding.btnConfirmDialog.visibility = View.GONE
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    // checkPermission from uses 1
    private fun checkPermission() : Boolean
    {
     if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
         ||ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
     {
        return true
     }
        return false
    }

    // to get user Permission 2
    private fun requestPermission()
    {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),PermissionID
        )
    }

    // to check location is enable 3
    private fun isLocationEnable() : Boolean
    {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //this function build in that check the permission result 4
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionID){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.d("Debug" , "you have the permission")
            }
        }
    }


    private fun initFusedLocationProviderClient()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
    }

    // check last Location 6
    private fun getLastLocation()
    {
        // check permission
        if (checkPermission()){
            // check permission is enable
            if (isLocationEnable()){
                // now get location
                fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task ->
                   var  location = task.result
                    if (location == null)
                    {
                        getNewLocation()
                    }
                    else
                    {
                        // set location
                        Log.d("Location","your Location " + location.altitude + "your country "
                                + getTheCountryName(location.latitude,location.longitude)+ "your city "
                                + getTheCityName(location.latitude,location.longitude))
                        dialogBinding.tvLocation.text = "your country  " + getTheCountryName(location.latitude,location.longitude)+ " ..your city "+ getTheCityName(location.latitude,location.longitude)

                    }
                }
            }
            else{
                Toast.makeText(this," please Enable Your Location ",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            requestPermission()
        }
    }

    // get new Location
    private fun getNewLocation()
    {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback,Looper.myLooper()
        )
    }

    //the locationCallback varriable
    private val locationCallback = object  : LocationCallback()
    {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            // set location
            Log.d("Location","your Location " + lastLocation.altitude + "your country "
                    + getTheCountryName(lastLocation.latitude,lastLocation.longitude))
            dialogBinding.tvLocation.text = "your country  " + getTheCountryName(lastLocation.latitude,lastLocation.longitude)+
                    " ..your city "+ getTheCityName(lastLocation.latitude,lastLocation.longitude)


        }
    }

    // get the city name
    private fun getTheCityName(lat : Double , long : Double) : String
    {
        var geoCoder = Geocoder(this@MainActivity, Locale.getDefault())
        var adress = geoCoder.getFromLocation(lat,long,1)
        cityName = adress.get(0).locality
        return cityName
    }

    // get the city country
    private fun getTheCountryName(lat : Double , long : Double) : String
    {
        var geoCoder = Geocoder(this@MainActivity, Locale.getDefault())
        var adress = geoCoder.getFromLocation(lat,long,1)
        countryName = adress.get(0).countryName
        return countryName
    }


}

