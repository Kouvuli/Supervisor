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
using FireSharp.Config;
using FireSharp.Response;
using FireSharp;
using System.Text.RegularExpressions;
using static SupervisorC.History;
using static SupervisorC.Schedule;
using System.Diagnostics;
using System.Windows.Threading;
using System.Globalization;
using System.IO;

namespace SupervisorC
{
    /// <summary>
    /// Interaction logic for Login.xaml
    /// </summary>
    public partial class Login : Window
    {
        FirebaseConfig config = new FirebaseConfig
        {
            AuthSecret = "AXTziauXqFYyXQ8RFFGCm5Hvlcw5ubDYkUiXUjiM",
            BasePath = "https://supervisor-fa34d-default-rtdb.asia-southeast1.firebasedatabase.app/",
        };
        FirebaseClient client;
        private int timeLeft;
        private int hour;
        private int min;
        private int sec;
        string passParent = "";
        string passChild = "";
        private int countWrong;
        DispatcherTimer timer;
        string path1;
        string projectDirectory;
        public Login()
        {
            InitializeComponent();
            countWrong = 0;
            client = new FirebaseClient(config);
            string workingDirectory = Environment.CurrentDirectory;
            projectDirectory = Directory.GetParent(workingDirectory).Parent.Parent.FullName;
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
            updateHistory(path1);
        }


        private async void Login_Click(object sender, RoutedEventArgs e)
        {
            if (String.IsNullOrEmpty(Password.Password))
            {
                MessageBox.Show("Chưa nhập mật khẩu");
                return;
            }
            try
            {
                string a=sender.ToString();
                string passChild = "";
                string passParent = "";
                
                (passChild,passParent)=await getPassword();
                
                
                if(Password.Password.ToString() == passParent)
                {
                    
                    timer = new DispatcherTimer();
                    //MessageBox.Show("Parent password");
                    timer.Tick += new EventHandler(Timer_Tick);
                    timer.Interval = new TimeSpan(0, 0, 1);
                    timeLeft = 7;
                    timer.Start();
                    loginBtn.IsEnabled = false;
                    //wait 60min 
                }
                else
                {
                    List<Schedule> todaySchedules = new List<Schedule>();
                    todaySchedules=await GetTodaySchedule();
                    Schedule nowSchedule = getNowSchedule(todaySchedules);
                    if (nowSchedule == null)
                    {
                        timer = new DispatcherTimer();
                        timer.Tick += new EventHandler(Timer_Tick1);
                        timer.Interval = new TimeSpan(0, 0, 1);
                        timeLeft = 15;
                        MessageBox.Show("Vẫn chưa được dùng máy");
                        timer.Start();
                        
                    }
                    else
                    {
                        if (Password.Password.ToString() == passChild)
                        {
                            this.Close();
                            MainWindow main = new MainWindow();
                            main.Show();
                            
                        }
                        else
                        {
                            countWrong++;
                            MessageBox.Show("Con " + (3 - countWrong).ToString()+" lan nhap nua");
                            if (countWrong == 3)
                            {
                                MessageBox.Show("10 phut nua may se tu dong tat");
                                Process.Start("shutdown", "/s /t 600");
                            }
                            
                        }
                    }
                    
                }
                //string[] ids = newh1.Split(new string[] { "},"},StringSplitOptions.None);
                //string[] ids1 = Regex.Split(newh1, @"\}\,");
                ////string newh1 = newh.Replace("{", "");
                ////string newh2 = newh1.Replace("}", "");
                ////string[] ids = newh2.Split(',');
                //string st = response.ResultAs<string>();
                //MessageBox.Show(st);
            }
            catch
            {
                MessageBox.Show("Error");
            }
        }

        private async Task<(string,string)> getPassword()
        {
            
            FirebaseResponse response = await client.GetAsync("User");
            string password = response.Body.ToString();
            password = password.Replace("\"", "");
            password = password.Substring(1, password.Length - 2);
            string[] passwords = password.Split(',');

            for (int i = 0; i < passwords.Length; i++)
            {
                string key = passwords[i].Split(':')[0];
                string value = passwords[i].Split(':')[1];
                if (key == "passParent")
                {
                    passParent = value;
                }
                else if (key == "passChild")
                {
                    passChild = value;
                }

            }
            return (passChild, passParent);
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
            
            foreach(Schedule schedule in schedules)
            {
                DateTime startTime = DateTime.ParseExact(schedule.timeStart, "HH:mm", CultureInfo.InvariantCulture);
                DateTime endTime = DateTime.ParseExact(schedule.timeEnd, "HH:mm", CultureInfo.InvariantCulture);
                if (moment>=startTime && moment<=endTime)
                {
                    return schedule;
                }

            }
            return null ;
        }
        private void Timer_Tick(object sender, EventArgs e)
        {
            
            if (timeLeft > 0) // 
            {
                timeLeft = timeLeft - 1; // decrement of timeleft on each tick
                hour = timeLeft / 3600; // Left hours
                min = (timeLeft - (hour * 3600)) / 60; //Left Minutes
                sec = timeLeft - (hour * 3600) - (min * 60); //Left Seconds
                hh.Text = hour.ToString();
                mm.Text = min.ToString(); // Setting minutes Text on each timer tick
                ss.Text = sec.ToString(); // Setting sec Text on each timer tick
            }
            else
            {
                timer.Stop(); // Stop Timer
                loginBtn.IsEnabled = true;              /* Process.Start("shutdown", "/s /t 0");*/
                //MessageBox.Show("Tat may");
                // Shutdown PC when Time is over
            }
            
        }
        private async void updateHistory(string path)
        {
            History temp = new History
            {
                date = DateTime.Now.ToString("dd/MM/yyyy"),
                timeStart = DateTime.Now.ToString("HH:mm"),
                timeEnd = "-1",
                keyLog = "-1",
            };
            PushResponse response = await client.PushAsync("History", temp);
            string a=response.Body.ToString();
            a = a.Replace("\"", "");
            a = a.Substring(1, a.Length - 2);
            string id = a.Substring(5, 20);
            using (StreamWriter outputFile = File.AppendText(path))
            {
                outputFile.Write(id);
            }
            return;
        }
        private void Timer_Tick1(object sender, EventArgs e)
        {
           
            if (timeLeft > 0) // 
            {
                timeLeft = timeLeft - 1; // decrement of timeleft on each tick
                hour = timeLeft / 3600; // Left hours
                min = (timeLeft - (hour * 3600)) / 60; //Left Minutes
                sec = timeLeft - (hour * 3600) - (min * 60); //Left Seconds
                hh.Text = hour.ToString();
                mm.Text = min.ToString(); // Setting minutes Text on each timer tick
                ss.Text = sec.ToString(); // Setting sec Text on each timer tick
                if (sender.ToString() != "")
                {
                    if (Password.Password.ToString() == passParent)
                    {
                        timer.Stop();
                        return;
                    }
                }
            }
            else
            {
                    
                timer.Stop(); // Stop Timer
                loginBtn.IsEnabled = true;
                //Process.Start("shutdown", "/s /t 0");                                 
                MessageBox.Show("Tat may");             // Shutdown PC when Time is over
            }
            
        }
    }
}
