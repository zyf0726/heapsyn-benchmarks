package thesis.cozy.redmine;

import thesis.common.IntMap;
import thesis.common.IntSet;

import java.util.NoSuchElementException;

public final class Redmine {

    private IntMap<Project> projects;
    private IntMap<Issue> issues;
    private IntSet issueTrackingModules;
    private IntSet closedIssueStatuses;

    private Redmine() {
        this.projects = IntMap.empty();
        this.issues = IntMap.empty();
        this.issueTrackingModules = IntSet.empty();
        this.closedIssueStatuses = IntSet.empty();
    }

    public static Redmine create() {
        return new Redmine();
    }

    public void insertProject(int projectId, int status) {
        if (IntMap.containsKey(projects, projectId))
            throw new IllegalArgumentException("duplicated project: " + projectId);
        projects = IntMap.put(projects, projectId, new Project(status, IntSet.empty()));
    }

    public void addProjectModule(int projectId, int module) {
        Project oldProject = IntMap.getOrThrow(projects, projectId);
        Project newProject = new Project(oldProject.status,
                IntSet.add(oldProject.modules, module));
        projects = IntMap.put(projects, projectId, newProject);
    }

    public void updateProjectStatus(int projectId, int newStatus) {
        Project oldProject = IntMap.getOrThrow(projects, projectId);
        Project newProject = new Project(newStatus, oldProject.modules);
        projects = IntMap.put(projects, projectId, newProject);
    }

    public void insertIssue(int issueId, int project, int assignedTo) {
        if (IntMap.containsKey(issues, issueId))
            throw new IllegalArgumentException("duplicated issue: " + issueId);
        if (!IntMap.containsKey(projects, project))
            throw new IllegalArgumentException("unknown project: " + project);
        issues = IntMap.put(issues, issueId, new Issue(project, IntSet.empty(), assignedTo));
    }

    public void addIssueStatus(int issueId, int status) {
        Issue oldIssue = IntMap.getOrThrow(issues, issueId);
        Issue newIssue = new Issue(oldIssue.project,
                IntSet.add(oldIssue.statuses, status),
                oldIssue.assignedTo);
        issues = IntMap.put(issues, issueId, newIssue);
    }

    public void markModuleAsIssueTracking(int module) {
        issueTrackingModules = IntSet.add(issueTrackingModules, module);
    }

    public void markIssueStatusAsClosed(int status) {
        closedIssueStatuses = IntSet.add(closedIssueStatuses, status);
    }

    private void print() {
        for (IntMap<Project> curr = projects; !IntMap.isEmpty(curr); curr = curr.next) {
            int projectId = curr.key;
            Project project = curr.value;
            System.out.printf("Project$%d: status=%d, modules=%s%n",
                    projectId, project.status, IntSet.toString(project.modules));
        }
        for (IntMap<Issue> curr = issues; !IntMap.isEmpty(curr); curr = curr.next) {
            int issueId = curr.key;
            Issue issue = curr.value;
            System.out.printf("Issue$%d: project=%d, statuses=%s, assignedTo=%d%n",
                    issueId, issue.project,
                    IntSet.toString(issue.statuses), issue.assignedTo);
        }
        System.out.println("issueTrackingModules: " + IntSet.toString(issueTrackingModules));
        System.out.println("closedIssueStatuses: " + IntSet.toString(closedIssueStatuses));
        System.out.println("==========");
    }

    public static void main(String[] args) {
        Redmine redmine = Redmine.create();
        redmine.insertProject(1, 0);
        redmine.addProjectModule(1, 20);
        redmine.insertIssue(100, 1, 500);
        redmine.addIssueStatus(100, 1);
        redmine.markIssueStatusAsClosed(2);
        redmine.print();
        redmine.updateProjectStatus(1, 1);
        redmine.addProjectModule(1, 10);
        redmine.addIssueStatus(100, 2);
        redmine.markModuleAsIssueTracking(10);
        redmine.markIssueStatusAsClosed(1);
        redmine.print();
        try {
            redmine = Redmine.create();
            redmine.insertProject(2, 1);
            redmine.insertProject(2, 1);
            System.out.println("ERROR: duplicated project insertion not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            redmine = Redmine.create();
            redmine.addProjectModule(3, 30);
            System.out.println("ERROR: addition of module to unknown project not detected");
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            redmine = Redmine.create();
            redmine.updateProjectStatus(4, 2);
            System.out.println("ERROR: update of unknown project not detected");
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            redmine = Redmine.create();
            redmine.insertProject(5, 1);
            redmine.insertIssue(200, 5, 600);
            redmine.insertIssue(200, 5, 600);
            System.out.println("ERROR: duplicated issue insertion not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            redmine = Redmine.create();
            redmine.insertIssue(200, 3, 600);
            System.out.println("ERROR: insertion of issue with unknown project not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            redmine = Redmine.create();
            redmine.addIssueStatus(300, 1);
            System.out.println("ERROR: addition of status to unknown issue not detected");
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

}
