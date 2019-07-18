# Blog 1 - Tuesday, 31/01/2017

This blog serves to document our experiences (both good and bad) as we develop our third year project. Although we will not set a locked upload schedule, we hope to keep it updated as often as possible in order to accurately convey how we are progressing in the following weeks.

In our week off after the January exams, we decided to get things moving ahead of schedule, so we could begin developing the actual Android application as soon as possible. In order to develop with the Android SDK, we needed to install and configure an IDE called Android Studio. This software is based off JetBrain's "Intellij" java development platform. This is the first time in college that we have ever required anything more than a fancy text editor such as Sublime or Atom, so getting used to a full-blown IDE is a challenge in itself!

![Screenshot of Android Studio](http://i.imgur.com/HpbCPYL.png)

As we will be developing different parts of the project at different times, we decided to make use of Android Studio's Git functionality. This allows us to use DCU's Gitlab server to not only keep our data backed up, but also keep track of all changes to our project.

Over on the server side of things, we now have a LAMP stack up and running. The operating system is Ubuntu Server 14.04.5 LTS, running Apache 2.4.7 alongside MySQL 5.6.33 and PHP 7.0.15. These versions are subject to change throughout development as updates are released.

---

# Blog 2 - Thursday, 02/02/2017

Just a few days on, we have finished developing the database schema. It is relatively small with a total of seven tables. We have ensured that the database is in first normal form (1NF), and we have maintained a consistent naming convention.

Development has begun on our API. To make an API call, our app will make a HTTP GET request to the server on port 80 in the following formats (may change over time):

```
http://api.cathal.xyz/index.php?action=login&email=test@example.com&password=secure
```

The API uses a MySQL user with a restricted view of the database. This is to limit unauthorized access to the database in case the API were for example, reverse engineered.

Passwords are encrypted using bcrypt (a hashing function based on the Whirlpool cipher). Here is an example API response:

![Example API response](http://i.imgur.com/EIVKFWh.png)

Looking ahead, we will finish work on the API and begin work on the Login/Register Android activities.

# Blog 3 - Saturday, 11/02/2017

Fast forward a week and we have finished the Login/Register activities, on both Android and server side. Graphically, the Activities have remained very barebones as we decided it was important to get as much functionality as possible before worrying about customizing the theme of our application.

![Login activity](http://i.imgur.com/H4EGAIn.png)

Users can login with either their username or email. Usernames must be 3-20 characters, can contain the characters [a-zA-Z_], and must begin with a letter. This is validated using Regular Expressions. As there are numerous points throughout our application which require string validation, we decided to move formatting logic into a new class, InputValidator. As of present, our password is only required to be 6 characters long, but we intend to increase the required password complexity in the future to prevent passwords from being easily cracked.

```
class InputValidator {
    static boolean isEmailValid(String input) {
        return EmailValidator.getInstance().isValid(input);
    }

    /*
     * Username must:
     * - begin with a letter
     * - be between 3 and 20 characters, inclusive
     * - contain only letters, numbers and underscores
     */
    static boolean isUsernameValid(String input) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]\\w{2,19}$");
        Matcher matcher = pattern.matcher(input);

        return matcher.find();
    }

    static boolean isPasswordValid(String input) {
        //TODO: Make stricter password requirements
        return !TextUtils.isEmpty(input) && input.length() >= 6;
    }
}

```

If the user has not created an account, they can easily create an account with a username, email and password.

![Register activity](http://i.imgur.com/O8Rk30A.png)

Moving onwards, we are currently developing the HomeActivity (which the user reaches upon login), and a global navigation bar which will be visible from all other Activities.

We are also trying to develop a way of sending an image file through a HTTP POST request, but this is proving difficult with the Android API. Previous popular methods use classes which are now deprecated, and Googles current HttpUrlConnection class is notoriously difficult to work with. This may take some time!

# Blog 4 - Monday, 20/02/2017

At this stage, we have decided to create a MainActivity class. Its purpose will be to control all communication between fragments that are connected to it, which includes every page after the user has logged in. The bottom navigation bar which allows the user to move between the HomeFragment, SavedAnswersFragment and UserProfileFragment is now working and displays these fragments in a container set up just above the bottom navigation bar. 

![Bottom navigation bar](http://i.imgur.com/3syABCt.png?1)

We are currently working on fragments that can be displayed when the user has clicked on the Home button. The HomeFragment class which displays the first screen shown when the user has just logged in or clicks on the home button, is now finished. This class sends the subject and year data from its spinners to the MainActivity class which then sends the data to the BrowseAnswersFragment once the “H” or “O” button is clicked.

![Home fragment](http://i.imgur.com/ICpzThr.png?1)

Saving and organising fragment states is proving to be difficult. We are using shared preferences to save states inside the HomeFragment, this may change in the future if we can find a better way.  The main difficulty at the moment in terms of states, is trying to move between screens using the back button. A method called addToBackStack takes a tag for each fragment and keeps track of the last fragment opened, by adding its tag to the top of a stack. This method is easy to use with fragments that just replace one another but when you have a navigation bar, things can get tricky. 

# Blog 5 - Tuesday, 21/02/2017

Much work has been done on the server backend in the last week. We have now have the ability to sign out, upload files, and browse answers. I don't think we've touched on the authentication side of things in a while, so I'll briefly walk through the process.

When a user attempts to sign in with a username and password, their password is hashed server-side using bcrypt, which outputs a 512 bit hash. This is one of the slowest hashing algorithms out there. An expert on the Hashcat forums barely managed 17 KH/s on an overclocked Nvidia Titan X. In comparison, the same user was able to hit 19 GH/s with the MD5 algorithm (literally over 1 million times faster).

If a matching set of credentials are found in the database, a session key is generated using MD5 so that the user does not need to sign in every time they use the app. MD5 is fine here because it does not need to cryptographically secure, it simply needs to be unique. This session key is stored with Android's SharedPreferences API, and is checked each time the user signs in.

Uploading files gave us a lot of trouble too. As we mentioned earlier, Google's HttpUrlConnection class is very tricky to deal with. This was made even more difficult by the fact that we aren't just sending text to the server, but binary files too. We ended up encoding the images in Base64 and sending it to the server as a string, then decoding server side (as recommended by many people online). The file is then stored on the server, and renamed with another unique hash, which is stored in the database. We have tested this functionality from our desktops, but not from an Android device as we haven't built the SubmitAnswer stuff in the application yet. One thing at a time!

In other news, we finally managed to track down a bug which was causing content fragments to overflow onto our bottom navigation bar. Successful day had by all..

# Blog 6 - Monday, 27/02/2017

Good progress has been made since the last blog. We now have fragments set up so that their states get saved. Only one fragment is created per fragment class and they can be re-used, using tags. 

The Back button now works well with every fragment. We created an interface to be used by all fragment classes, that will set up a method in each fragment called onBackPress. This method will be called when the back button is pressed and will remove the current fragment from the screen and replace it with the previous one.

```
@Override
public void onBackPress() {
    ((MainActivity) getActivity()).popFragment();
}
```

We set up a HashMap which maps tags to stacks which hold instances of each fragment class and keeps bottom navigation bar fragments separate, based on which menu item they belong to. This also stops new instances of fragments being created, when an instance for that fragment already exists. 

Our BrowseAnswersFragment now displays a list of dummy answers pulled from the database on our server. We need to work on the way the list will be displayed in terms of theme and customisation, but as we’ve mentioned in previous blogs, we are leaving all that to the end.

![Browse answers fragment](http://i.imgur.com/CwUIXG4.png?1)

We are now working on what will be displayed when an answer from the list is clicked on. We currently have this DisplayAnswersFragment already obtaining the answer objects being sent to it. It is also displaying the the title and content of the answer, but buttons for the upvote system and reporting inappropriate content still need to be added. 

Fragment scrolling is something that needs to be looked into, so we can have a comment section just below the buttons on this page, that will be able to grow as comments are added.

Once we get all the fragments contained within our Home button up to speed, we will move on to setting up our internal database, so saved answers can be accessed when the user doesn’t have internet connection.

# Blog 7 - Thursday, 02/03/2017

Two more Activities have made their way into our app - CameraActivity and PasswordActivity. The CameraActivity allows the user to use their phone's camera to take a picture within our application, and upload it to our server with the solution they are submitting. This was a tricky one to implement, as it involves communicating with Android's built in camera application, something we have never seen before. With Google's latest security features, accessing files is no longer as easy as using Java's File class. You now have to register a FileProvider with the application which grants read/write permissions to specific URIs. Normally you are free to modify your private application data however you please, but because the camera works with shared data (so other applications can use the images) it gets a bit fiddly.

The PasswordActivity allows the user to update their existing password. Both their old and new passwords are sent to the server, verified, and updated accordingly.

In an earlier blog, we mentioned that we had designed the FileUpload functionality but hadn't tested it from the Android client yet. We have since managed to finish and test this code - our app can now upload not one but multiple files per solution. This was another serious headache, as Google's documentation on their Gallery API is poor to say the least. We eventually got it working after thorough research online and many hours of blood, sweat and tears.

Finally, we have also begun work documenting our code as per Javadoc standards. This will enable us to compile our code into Oracle-style documentation.

# Blog 8 - Monday, 06/03/2017

Entering our last few days of work, we still have plenty of work to do. Having said that, we have come leaps and bounds since last week, and our app is finally coming together to actually look like.. an app. Most of our activities are now finished and working.

We have begun working on a baby-blue theme to accompany our app:

![Home activity](http://i.imgur.com/mgNVS9c.png)

We have finished the reputation buttons on the DisplayAnswer fragment, so now users can rate each other's solutions. The most popular solutions will appear first in the search list.

The most important (and difficult) part of the app we completed is the SavedAnswers fragment. When you are browsing solutions, you may wish to store one for offline use. Allowing this feature comprised of a lot of work. First we had to replicate parts of the online database offline using SQLite3. Then we had to configure the storage directories (again) as covered in a previous blog. To both help with saved answers and help with the general speed of the app, we decided to implement a caching solution. Whenver a user opens a solution, the images in the solution are being downloaded, so why not simply save them permanently for future use? Here is a sample of what our files table looks like on the database:

![files table in database](http://i.imgur.com/9OK9cYY.png)

We are currently working on the comments section, but after that we will be designing a theme for the remainder of the application, and will begin extensive testing.

# Blog 9 - Wednesday, 08/03/2017

This blog will be dedicated to the testing of our application.

## 1. Unit Testing
Thoroughly testing our application proved to be far more difficult than we first thought. Although the majority of our codebase is written in Java, it is buried deep within the Android framework. Usually, JUnit would be perfect for this kind of thing. Even though JUnit is an entire framework in itself, it is actually quite simple to learn the basics. Unfortunately for us, JUnit for Android doesn't quite qualify as "the basics".

We did the best we could, and wrote JUnit tests for all of our "local" units - that is, units which have no dependencies on the Android framework. Here we can see our InputValidatorTest:

```
public class InputValidatorTest {

    /*
     * isEmailValid
     */
    @Test
    public void isEmailValid_Correct_ReturnsTrue() {
        assertTrue(InputValidator.isEmailValid("test@example.com"));
        assertTrue(InputValidator.isEmailValid("a@b.co.uk"));
        assertTrue(InputValidator.isEmailValid("john.f.kennedy@whitehouse.gov"));
        assertTrue(InputValidator.isEmailValid("abc-123@four56.org"));
    }

    @Test
    public void isEmailValid_Incorrect_ReturnsFalse() {
        assertFalse(InputValidator.isEmailValid("test@example#.com"));
        assertFalse(InputValidator.isEmailValid("t[e]st@example.com"));
        assertFalse(InputValidator.isEmailValid("@motorola@example.com"));
        assertFalse(InputValidator.isEmailValid("l+quid=@;.com"));
    }

    /*
     * isUsernameValid
     */
    @Test
    public void isUsernameValid_Correct_ReturnsTrue() {
        assertTrue(InputValidator.isUsernameValid("abc"));
        assertTrue(InputValidator.isUsernameValid("abc123"));
        assertTrue(InputValidator.isUsernameValid("abc_123"));
        assertTrue(InputValidator.isUsernameValid("test_number_4444"));
    }

    @Test
    public void isUsernameValid_Incorrect_ReturnsFalse() {
        assertFalse(InputValidator.isUsernameValid("a")); // too short
        assertFalse(InputValidator.isUsernameValid("123abc")); // first char not letter
        assertFalse(InputValidator.isUsernameValid("_abc123")); // first char not letter
        assertFalse(InputValidator.isUsernameValid("test_number_44448888h")); // too long
    }

    /*
     * isPasswordValid
     */
    @Test
    public void isPasswordValid_Correct_ReturnsTrue() {
        assertTrue(InputValidator.isPasswordValid("8Ac8'@daw"));
        assertTrue(InputValidator.isPasswordValid("%&*jfFe5pf#"));
        assertTrue(InputValidator.isPasswordValid("*&NU2iufib@}"));
        assertTrue(InputValidator.isPasswordValid("%^*nfj6nbY#'a"));
        assertTrue(InputValidator.isPasswordValid("Niall69!"));
    }

    @Test
    public void isPasswordValid_Incorrect_ReturnsFalse() {
        assertFalse(InputValidator.isPasswordValid("abCfnf*")); // too short
        assertFalse(InputValidator.isPasswordValid("ahdka&*&*")); // no upper case
        assertFalse(InputValidator.isPasswordValid("A*&BISFDS")); // no lower case
        assertFalse(InputValidator.isPasswordValid("AUHIUbfhbfhsA")); // no special chars
    }

    /*
     * isTitleValid
     */
    @Test
    public void isTitleValid_Correct_ReturnsTrue() {
        assertTrue(InputValidator.isTitleValid("This is a valid Title!"));
        assertTrue(InputValidator.isTitleValid("Another similarly valid 93qhr9utitel !"));
    }

    @Test
    public void isTitleValid_inCorrect_ReturnsFalse() {
        assertFalse(InputValidator.isTitleValid("22")); // too short
    }

    /*
     * isBodyValid
     */
    @Test
    public void isBodyValid_Correct_ReturnsTrue() {
        assertTrue(InputValidator.isBodyValid("valid body"));
        assertTrue(InputValidator.isBodyValid("v"));
    }

    @Test
    public void isBodyValid_inCorrect_ReturnsFalse() {
        assertFalse(InputValidator.isBodyValid("")); // too short
    }
}
```

The great thing about writing these Unit tests is that every time a piece of testable code within our project is changed, the tests are automatically performed on the new code to ensure that any changes made still perform as well as the original code.

We attempted to write some Unit test cases for our API code server-side, but we ran into a few problems. Firstly, PHPUnit (the PHP equivalent of JUnit) requires all PHP code to be object-oriented, and designed to produce specific Exceptions. We never foresaw this problem during development, and almost all of our server code is procedural.

We therefore attempted to write our own unit tests, but quickly realised that with the way our API architecture was designed, there was very little to test! For example, all of our API calls essentially run a validation test on all user input, before any other instructions are executed. Let's take a look at the uploadFile script:

```
<?php

if (!isset($_POST['solution']) || !isset($_POST['extension']) || !isset($_POST['file'])) {
    respond('bad_request');
}

checkSession();

$user = Database::getInstance()->get('users', ['session', '=', $_GET['session']])->first();
$solution = $_POST['solution'];
$extension = $_POST['extension'];
$file_contents = base64_decode($_POST['file']);
$hash = md5(uniqid(rand(), true));

file_put_contents("../uploads/" . $hash . '.' . $extension, $file_contents);

Database::getInstance()->insert('files', [
    'user_id'   => $user->id,
    'hash'      => $hash,
    'extension' => $extension
]);

$file = Database::getInstance()->get('files', ['hash', '=', $hash])->first();
$response['file'] = $file;

Database::getInstance()->insert('solutions_files', [
    'solution_id'   => $solution,
    'file_id'       => $file->id
]);

respond();
```

Before any data is sent to our server, it is thoroughly inspected by the client. Once it reaches our server, it is then verified to be intact, and handed over to our database script (most API calls deal with the database). The database then uses prepared statements to carefully communicate with the database, eliminating the chance of SQL injection attacks.

## 2. Usability Testing

As the developers of our application, we have spent literally hundreds of hours trawling through our own code, yet ironically we can become blinded to to our applications most obvious pitfalls. To work around this, we decided to ask one of ours friends studying Engineering in DCU to test out our app and provide us with feedback.

After giving him a brief description of what the app does (so he had some context of what was going on), we gave him a list of objectives which he had to achieve, and then closely watched him as he attempted to complete each task. In line with our original project requirements, his objectives were:

1. Create an account
2. Sign in with the newly created account
3. Search for solutions to a particular exam paper
    - Enlarge any images the solution may have
    - Report the solution
    - Vote the solution up or down
4. Save the answer for offline use
5. View the saved answer (with the internet disabled)
6. Submit a new solution to a particular exam paper
7. Reply to any exam paper (and delete the reply again)
8. Change the password
9. Sign out

This proved to be extremely effective! We were able to identify many weaknesses in our application which we simply never saw as problems, presumably because we were just so used to the app as it was. See the following table for the results:

| Objective | Problem(s)                                                                                                                             | Resolution(s)                                                                                                                                                                                   |
|-----------|----------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1         | (a) Didn't see the "Create account button" (b) Was not clear why his password was not accepted (c) Password requirements were far too strict | (a) Changed "create account" to "Don't have an account?", and increased the font size (b) Implemented a clear message explaining the password requirements (c) Made password requirements less strict |
| 2         | N/A                                                                                                                                    | N/A                                                                                                                                                                                             |
| 3         | N/A                                                                                                                                    | N/A                                                                                                                                                                                             |
| 4         | N/A                                                                                                                                    | N/A                                                                                                                                                                                             |
| 5         | N/A                                                                                                                                    | N/A                                                                                                                                                                                             |
| 6         | Was not clear whether selected images had actually been selected                                                                       | Implemented a clear message to confirm that the images had been selected                                                                                                                        |
| 7         | N/A                                                                                                                                    | N/A                                                                                                                                                                                             |
| 8         | N/A                                                                                                                                    | N/A                                                                                                                                                                                             |
| 9         | N/A                                                                                                                                    | N/A                                                                                                                                                                                             |

Next line

# Blog 10 - Thursday, 09/03/2017

Since the last blog we have made good progress. We have completed our last large functionality change to the app, which was adding a comment section in the display answer fragment. A user can now write comments under any answer and delete them by pressing the trash icon on the answer they wish to delete.

Our report button now flags answers, and the username of the answer author now gets displayed as you can see below.

![Report Button](http://i.imgur.com/N55aW4X.png?1)

![Comment section](http://i.imgur.com/YuEm2BP.png?1)

Currently, we’re working on our documentation and testing which is a slow and frustrating process but it feels like we’re on track. Once we have these tasks complete we will try to polish off the app, make it look nice and fix any small bugs that we can find.

