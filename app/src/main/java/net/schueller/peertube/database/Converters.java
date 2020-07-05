package net.schueller.peertube.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.schueller.peertube.model.Account;
import net.schueller.peertube.model.Category;
import net.schueller.peertube.model.Channel;
import net.schueller.peertube.model.File;
import net.schueller.peertube.model.Language;
import net.schueller.peertube.model.Licence;
import net.schueller.peertube.model.Privacy;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Converters implements Serializable {

    @TypeConverter // note this annotation
    public String tags(ArrayList<String> tags) {
        if (tags == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        String json = gson.toJson(tags, type);
        return json;
    }

    @TypeConverter // note this annotation
    public ArrayList<String> toTags(String tagString) {
        if (tagString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
       ArrayList<String> tagsList = gson.fromJson(tagString, type);
        return tagsList;
    }



    @TypeConverter
    public String Account(Account account) {
        if (account == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Account>() {
        }.getType();
        String json = gson.toJson(account, type);
        return json;
    }

    @TypeConverter // note this annotation
    public Account toAccount(String accountString) {
        if (accountString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Account>() {
        }.getType();
        Account account = gson.fromJson(accountString, type);
        return account;
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
    @TypeConverter
    public String Category(Category category) {
        if (category == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Category>() {
        }.getType();
        String json = gson.toJson(category, type);
        return json;
    }

    @TypeConverter
    public Category toCategory(String categoryString) {
        if (categoryString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Category>() {
        }.getType();
        Category category = gson.fromJson(categoryString, type);
        return category;
    }
    @TypeConverter
    public String License(Licence license) {
        if (license == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Licence>() {
        }.getType();
        String json = gson.toJson(license, type);
        return json;
    }

    @TypeConverter
    public Licence toLicense(String licenseString) {
        if (licenseString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Licence>() {
        }.getType();
        Licence license = gson.fromJson(licenseString, type);
        return license;
    }
    @TypeConverter
    public String Language(Language language) {
        if (language == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Language>() {
        }.getType();
        String json = gson.toJson(language, type);
        return json;
    }

    @TypeConverter
    public Language toLanguage(String languageString) {
        if (languageString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Language>() {
        }.getType();
        Language language = gson.fromJson(languageString, type);
        return language;
    }
    @TypeConverter
    public String Channel(Channel channel) {
        if (channel == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Channel>() {
        }.getType();
        String json = gson.toJson(channel, type);
        return json;
    }

    @TypeConverter
    public Channel toChannel(String channelString) {
        if (channelString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Channel>() {
        }.getType();
        Channel channel = gson.fromJson(channelString, type);
        return channel;
    }
    @TypeConverter
    public String Privacy(Privacy privacy) {
        if (privacy == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Privacy>() {
        }.getType();
        String json = gson.toJson(privacy, type);
        return json;
    }

    @TypeConverter
    public Privacy toPrivacy(String privacyString) {
        if (privacyString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Privacy>() {
        }.getType();
        Privacy privacy = gson.fromJson(privacyString, type);
        return privacy;
    }
    @TypeConverter
    public String files(ArrayList<File> files) {
        if (files == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<File>>() {
        }.getType();
        String json = gson.toJson(files, type);
        return json;
    }

    @TypeConverter // note this annotation
    public ArrayList<File> toFiles(String fileString) {
        if (fileString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<File>>() {
        }.getType();
        ArrayList<File> fileList = gson.fromJson(fileString, type);
        return fileList;
    }


}