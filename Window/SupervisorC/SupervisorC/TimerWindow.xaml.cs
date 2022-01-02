using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.Text.RegularExpressions;
using FireSharp;
using FireSharp.Config;
using FireSharp.Response;
using System.Globalization;
using System.Threading;

namespace SupervisorC
{
    /// <summary>
    /// Interaction logic for TimerWindow.xaml
    /// </summary>
    public partial class TimerWindow : Window
    {
        private System.Timers.Timer timer;
        private int timeLeft;
        private int hour;
        private int min;
        private int sec;
        FirebaseClient client;
        List<Schedule> todaySchedule;
        Schedule nowSchedule;
        FirebaseConfig config = new FirebaseConfig
        {
            AuthSecret = "AXTziauXqFYyXQ8RFFGCm5Hvlcw5ubDYkUiXUjiM",
            BasePath = "https://supervisor-fa34d-default-rtdb.asia-southeast1.firebasedatabase.app/",
        };
        public TimerWindow()
        {
            InitializeComponent();

            timer = new System.Timers.Timer();
            client = new FirebaseClient(config);
            getData();
            
            
        }
        private async void getData()
        {
            todaySchedule = await GetTodaySchedule();
            nowSchedule = getNowSchedule(todaySchedule);
            timeLeft = Int32.Parse(nowSchedule.interruptTime) * 60;
            timer.Interval = 1000;
            timer.Elapsed += Timer_Tick;
            timer.Enabled = true;
        }
        private async Task<List<Schedule>> GetTodaySchedule()
        {
            List<Schedule> schedules = new List<Schedule>();
            List<string> ids = new List<string>();
            //string today = "16/12/2021";
            string today = DateTime.Now.ToString("dd/MM/yyyy");
            FirebaseResponse response1 = await client.GetAsync("Schedule");
            string a = response1.Body.ToString();
            a = a.Replace("\"", "");
            a = a.Substring(1, a.Length - 2);
            string[] parts2 = Regex.Split(a, @"\}\,");
            for (int i = 0; i < parts2.Length; i++)
            {
                ids.Add(parts2[i].Substring(0, 20));
            }
            foreach (string id in ids)
            {
                FirebaseResponse response = await client.GetAsync("Schedule/" + id);
                Schedule temp = response.ResultAs<Schedule>();
                schedules.Add(temp);
            }
            List<Schedule> newSche = new List<Schedule>();
            foreach (Schedule sche in schedules)
            {
                if (sche.date.Equals(today))
                {
                    newSche.Add(sche);
                }
            }
            return newSche;
        }
        private Schedule getNowSchedule(List<Schedule> schedules)
        {
            string time = DateTime.Now.ToString("HH:mm");
            DateTime moment = DateTime.ParseExact(time, "HH:mm", CultureInfo.InvariantCulture);

            foreach (Schedule schedule in schedules)
            {
                DateTime startTime = DateTime.ParseExact(schedule.timeStart, "HH:mm", CultureInfo.InvariantCulture);
                DateTime endTime = DateTime.ParseExact(schedule.timeEnd, "HH:mm", CultureInfo.InvariantCulture);
                if (moment >= startTime && moment <= endTime)
                {
                    return schedule;
                }

            }
            return null;
        }
        private void Timer_Tick(object sender, EventArgs e)
        {

            if (timeLeft > 0) // 
            {
                timeLeft = timeLeft - 1; // decrement of timeleft on each tick
                hour = timeLeft / 3600; // Left hours
                min = (timeLeft - (hour * 3600)) / 60; //Left Minutes
                sec = timeLeft - (hour * 3600) - (min * 60); //Left Seconds
                this.Dispatcher.Invoke(() =>
                {
                    hours.Text = hour.ToString();
                    mins.Text = min.ToString(); // Setting minutes Text on each timer tick
                    secs.Text = sec.ToString(); // Setting sec Text on each timer tick
                });
                
            }
            else
            {
                timer.Stop(); // Stop Timer
                
                Thread t = new Thread(() =>
                {

                    this.Dispatcher.Invoke(() =>
                    {
                        MainWindow main = new MainWindow();
                        main.Show();
                        this.Close();
                    });

                });
                t.SetApartmentState(ApartmentState.STA);
                t.Start();
                /* Process.Start("shutdown", "/s /t 0");*/
                //MessageBox.Show("Tat may");
                // Shutdown PC when Time is over
            }

        }
    }
}
