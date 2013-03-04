package org.staxer.util.words;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com) (akerigan@gmail.com)
 * Date: 25.03.2008
 * Time: 22:09:18
 */
public enum Sex {
    male(0),
    female(1);

    int id;

    Sex(int id) {
        this.id = id;
    }
}
