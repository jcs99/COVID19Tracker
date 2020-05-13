package pt.ipsantarem.esgts.covid19tracker.server;

import pt.ipsantarem.esgts.covid19tracker.server.interfaces.Tree;
import pt.ipsantarem.esgts.covid19tracker.server.models.NewDeathsNode;
import pt.ipsantarem.esgts.covid19tracker.server.models.abstracts.AVLVirusTree;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerMain {
    public static void main(String[] args) {
        NewDeathsNode newCasesNode =
                new NewDeathsNode(parseDate("2020-05-13"), "Portugal", 693);
        Tree<Date, Integer, NewDeathsNode> newCasesTree = new AVLVirusTree<>(newCasesNode);
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-14"), "Portugal", 413));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-15"), "Portugal", 121));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-16"), "Portugal", 98));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-17"), "Portugal", 234));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-18"), "Portugal", 871));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-19"), "Portugal", 532));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-20"), "Portugal", 489));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-21"), "Portugal", 671));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-22"), "Portugal", 812));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-23"), "Portugal", 456));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-24"), "Portugal", 431));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-25"), "Portugal", 278));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-26"), "Portugal", 1123));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-27"), "Portugal", 781));
        newCasesTree.add(new NewDeathsNode(parseDate("2020-05-28"), "Portugal", 901));
        System.out.println(newCasesTree.inorder());
    }

    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}