package fr.xebia.confluence2wordpress.wp;



public class WordpressUser {

    private Integer id;

    private String login;

    private String niceName;

    private String displayName;

    private String firstName;

    private String lastName;

    private Integer level;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getLogin() {
        return login;
    }


    public void setLogin(String login) {
        this.login = login;
    }


    public String getNiceName() {
        return niceName;
    }


    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }


    public String getDisplayName() {
        return displayName;
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastNameName) {
        this.lastName = lastNameName;
    }


    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    //http://codex.wordpress.org/Roles_and_Capabilities#User_Level_to_Role_Conversion

    public boolean isSubscriber() {
        return level != null && level >= 0;
    }
    public boolean isContributor() {
        return level != null && level >= 1;
    }
    public boolean isAuthor() {
        return level != null && level >= 2;
    }
    public boolean isEditor() {
        return level != null && level >= 5;
    }
    public boolean isAdministrator() {
        return level != null && level >= 8;
    }

    @Override
    public String toString() {
        return String.format(
            "WordpressUser [id=%s, login=%s, level=%s, displayName=%s, niceName=%s, firstName=%s, lastName=%s]",
            id, login, level, displayName, niceName, firstName, lastName);
    }



}