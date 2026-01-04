package thesis.cozy.openfire;

import thesis.common.IntMap;
import thesis.common.IntSet;

public final class RosterCore {

    private IntSet users;
    private IntMap<RosterItem> rosterItems;
    private IntMap<Group> groups;

    private RosterCore() {
        this.users = IntSet.empty();
        this.rosterItems = IntMap.empty();
        this.groups = IntMap.empty();
    }

    public static RosterCore create() {
        return new RosterCore();
    }

    public void addUser(int userId) {
        users = IntSet.add(users, userId);
    }

    public void removeUser(int userId) {
        for (IntMap<Group> curr = groups; !IntMap.isEmpty(curr); curr = curr.next) {
            int gid = curr.key;
            Group g = curr.value;
            if (IntSet.contains(g.members, userId))
                throw new IllegalArgumentException("user " + userId + " is still in group " + gid);
        }
        users = IntSet.remove(users, userId);
    }

    public void addRosterItem(int itemId, int userId, int targetId) {
        if (IntMap.containsKey(rosterItems, itemId))
            throw new IllegalArgumentException("duplicated roster item: " + itemId);
        for (IntMap<RosterItem> curr = rosterItems; !IntMap.isEmpty(curr); curr = curr.next) {
            RosterItem item = curr.value;
            if (item.user == userId && item.target == targetId) {
                String message = "redundant roster item for user " +
                                 userId + " and target " + targetId +
                                 ": existing item id " + curr.key + ", new item id " + itemId;
                throw new IllegalArgumentException(message);
            }
        }
        rosterItems = IntMap.put(rosterItems, itemId, new RosterItem(userId, targetId));
    }

    public void removeRosterItem(int itemId) {
        rosterItems = IntMap.remove(rosterItems, itemId);
    }

    public void addGroup(int groupId, int rosterMode) {
        if (IntMap.containsKey(groups, groupId))
            throw new IllegalArgumentException("duplicated group: " + groupId);
        groups = IntMap.put(groups, groupId, new Group(rosterMode, IntSet.empty(), IntSet.empty()));
    }

    public void removeGroup(int groupId) {
        if (!IntMap.containsKey(groups, groupId))
            throw new IllegalArgumentException("unknown group: " + groupId);
        Group g = IntMap.getOrThrow(groups, groupId);
        if (!IntSet.isEmpty(g.members))
            throw new IllegalArgumentException("group " + groupId + " is not empty");
        if (!IntSet.isEmpty(g.children))
            throw new IllegalArgumentException("group " + groupId + " has children");
        for (IntMap<Group> curr = groups; !IntMap.isEmpty(curr); curr = curr.next) {
            int parentId = curr.key;
            Group parent = curr.value;
            if (IntSet.contains(parent.children, groupId)) {
                String message = "group " + groupId + " is still a child of group " + parentId;
                throw new IllegalArgumentException(message);
            }
        }
        groups = IntMap.remove(groups, groupId);
    }

    public void addMember(int groupId, int userId) {
        if (!IntMap.containsKey(groups, groupId))
            throw new IllegalArgumentException("unknown group: " + groupId);
        if (!IntSet.contains(users, userId))
            throw new IllegalArgumentException("unknown user: " + userId);
        Group oldGroup = IntMap.getOrThrow(groups, groupId);
        Group newGroup = new Group(oldGroup.rosterMode,
                oldGroup.children,
                IntSet.add(oldGroup.members, userId));
        groups = IntMap.put(groups, groupId, newGroup);
    }

    public void removeMember(int groupId, int userId) {
        if (!IntMap.containsKey(groups, groupId))
            throw new IllegalArgumentException("unknown group: " + groupId);
        Group oldGroup = IntMap.getOrThrow(groups, groupId);
        Group newGroup = new Group(oldGroup.rosterMode,
                oldGroup.children,
                IntSet.remove(oldGroup.members, userId));
        groups = IntMap.put(groups, groupId, newGroup);
    }

    public void addSharedGroup(int parentGroupId, int childGroupId) {
        if (!IntMap.containsKey(groups, parentGroupId))
            throw new IllegalArgumentException("unknown parent group: " + parentGroupId);
        if (!IntMap.containsKey(groups, childGroupId))
            throw new IllegalArgumentException("unknown child group: " + childGroupId);
        Group oldParent = IntMap.getOrThrow(groups, parentGroupId);
        if (oldParent.rosterMode != Group.ONLY_GROUP) {
            String message = "parent group " + parentGroupId + " is not in ONLY_GROUP mode";
            throw new IllegalArgumentException(message);
        }
        Group newParent = new Group(oldParent.rosterMode,
                IntSet.add(oldParent.children, childGroupId),
                oldParent.members);
        groups = IntMap.put(groups, parentGroupId, newParent);
    }

    public void removeSharedGroup(int parentGroupId, int childGroupId) {
        if (!IntMap.containsKey(groups, parentGroupId))
            throw new IllegalArgumentException("unknown parent group: " + parentGroupId);
        Group oldParent = IntMap.getOrThrow(groups, parentGroupId);
        Group newParent = new Group(oldParent.rosterMode,
                IntSet.remove(oldParent.children, childGroupId),
                oldParent.members);
        groups = IntMap.put(groups, parentGroupId, newParent);
    }

    public void setMode(int groupId, int rosterMode) {
        if (!IntMap.containsKey(groups, groupId))
            throw new IllegalArgumentException("unknown group: " + groupId);
        Group oldGroup = IntMap.getOrThrow(groups, groupId);
        if (oldGroup.rosterMode == Group.ONLY_GROUP && !IntSet.isEmpty(oldGroup.children)) {
            String message = "cannot change mode of group " + groupId +
                             " from ONLY_GROUP while it has children";
            throw new IllegalArgumentException(message);
        }
        Group newGroup = new Group(rosterMode, oldGroup.children, oldGroup.members);
        groups = IntMap.put(groups, groupId, newGroup);
    }

    void print() {
        System.out.println("users: " + IntSet.toString(users));
        for (IntMap<RosterItem> curr = rosterItems; !IntMap.isEmpty(curr); curr = curr.next) {
            int itemId = curr.key;
            RosterItem item = curr.value;
            System.out.printf("RosterItem$%d: user=%d, target=%d\n",
                    itemId, item.user, item.target);
        }
        for (IntMap<Group> curr = groups; !IntMap.isEmpty(curr); curr = curr.next) {
            int groupId = curr.key;
            Group g = curr.value;
            System.out.printf("Group$%d: rosterMode=%d, children=%s, members=%s\n",
                    groupId, g.rosterMode,
                    IntSet.toString(g.children), IntSet.toString(g.members));
        }
    }

}
