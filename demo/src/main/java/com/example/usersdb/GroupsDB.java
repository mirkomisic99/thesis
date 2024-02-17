package com.example.usersdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.objects.User;

public class GroupsDB {
    private static GroupsDB groupsDB = new GroupsDB();

    private Map<String, List<User>> usersGroup = new HashMap<>();
    private List<String> groupNames = new ArrayList<>();

    private GroupsDB() {
    }

    public static GroupsDB getInstanceOf() {
        return groupsDB;
    }

    public void addNewGroup(String groupName, List<User> usersList) {
        usersGroup.put(groupName, usersList);
        groupNames.add(groupName);
    }

    public Map<String, List<User>> getGroups() {
        return this.usersGroup;
    }

    public List<String> getGroupNames() {
        return this.groupNames;
    }

    public List<Integer> returnUserPortsForGroupName(String groupName) {
        List<Integer> listToReturn = new ArrayList<>();

        for (Map.Entry<String, List<User>> eachGroup : usersGroup.entrySet()) {
            if (eachGroup.getKey().equals(groupName)) {
                for (User eachUser : eachGroup.getValue())
                    listToReturn.add(eachUser.getPort());
            }
        }

        return listToReturn;
    }

    public void leaveGroup(User user, String groupName) {


        List<User> newList = new ArrayList<>();
        for (Map.Entry<String, List<User>> eachGroup : usersGroup.entrySet()) {
            if(eachGroup.getKey().equals(groupName)){
                for(User eachUser : eachGroup.getValue()){
                    if(!(eachUser.getUsername().equals(user.getUsername())))
                        newList.add(eachUser);
                }
            }
            
        }
        usersGroup.put(groupName, newList);

    }

    public void deleteGroup(String groupName) {
        groupNames.remove(groupName);
        usersGroup.remove(groupName);
    }
}
