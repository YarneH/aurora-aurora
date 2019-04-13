package android.content;

public class ComponentName {
    private String mPackageName;
    private String mClassName;

    public ComponentName(String packageName, String cls) {
        mPackageName = packageName;
        mClassName = cls;
    }

    public String getPackageName() {
        return mPackageName;
    }
}
