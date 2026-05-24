package s3;

import java.util.HashMap;
import java.util.Map;

public class AccessControlList {
    private final User owner;
    private final Map<User, Permission> permissions = new HashMap<>();

    public AccessControlList(User owner) {
        this.owner = owner;
        permissions.put(owner, Permission.FULL_CONTROL);
    }

    public User getOwner() {
        return owner;
    }

    public synchronized void grantPermission(User user, Permission perm) {
        permissions.put(user, perm);
    }

    public synchronized boolean hasPermission(User user, Permission perm) {
        if (user.equals(owner)) {
            return true;
        }
        Permission userPerm = permissions.get(user);
        if (userPerm == Permission.FULL_CONTROL) {
            return true;
        }
        return userPerm == perm;
    }
}
