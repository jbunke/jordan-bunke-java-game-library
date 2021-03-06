package com.jordanbunke.jbjgl.io;

import com.jordanbunke.jbjgl.events.*;
import com.jordanbunke.jbjgl.utility.CollectionProcessing;
import com.jordanbunke.jbjgl.utility.JBJGLGlobal;
import com.jordanbunke.jbjgl.utility.RenderConstants;
import com.jordanbunke.jbjgl.window.JBJGLCanvas;

import java.awt.event.*;
import java.util.*;
import java.util.stream.Collectors;

public class JBJGLListener implements
        KeyListener, MouseListener, MouseMotionListener, WindowListener {

    private final List<JBJGLInputTask> tasks;

    private final List<JBJGLEvent> eventList;
    private final Map<JBJGLKey, Boolean> characterPressedStatusMap;
    private final int[] mousePosition;

    private JBJGLListener(final JBJGLCanvas canvas) {
        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);

        tasks = new ArrayList<>();

        eventList = new ArrayList<>();
        characterPressedStatusMap = new HashMap<>();
        mousePosition = new int[2];
    }

    public static JBJGLListener create(final JBJGLCanvas canvas) {
        return new JBJGLListener(canvas);
    }

    public void checkForMatchingKeyStroke(
            final JBJGLKeyEvent toMatch, final Runnable behaviour
    ) {
        List<JBJGLEvent> eventList = getUnprocessedEvents();

        for (JBJGLEvent event : eventList) {
            if (event.isProcessed())
                continue;

            if (event.equals(toMatch)) {
                behaviour.run();
                event.markAsProcessed();
            }
        }
    }

    public void checkForMatchingKeyStroke(
            final JBJGLKey key, final JBJGLKeyEvent.Action action,
            final Runnable behaviour
    ) {
        JBJGLKeyEvent toMatch = JBJGLKeyEvent.generate(key, action);
        checkForMatchingKeyStroke(toMatch, behaviour);
    }

    public List<JBJGLEvent> getUnprocessedEvents() {
        try {
            return List.copyOf(eventList).stream()
                    .filter(x -> !x.isProcessed()).collect(Collectors.toList());
        } catch (ConcurrentModificationException e) {
            JBJGLGlobal.printErrorToJBJGLChannel(
                    "Attempted to fetch unprocessed input events as one occurred. Events were not returned."
            );
            return new ArrayList<>();
        }
    }

    public void emptyEventList() {
        CollectionProcessing.emptyList(eventList);
    }

    public void addTask(final JBJGLInputTask task) {
        tasks.add(task);
    }

    public void addTasks(final Collection<JBJGLInputTask> tasks) {
        this.tasks.addAll(tasks);
    }

    public void removeTask(final JBJGLInputTask task) {
        tasks.remove(task);
    }

    public void emptyTasks() {
        CollectionProcessing.emptyList(tasks);
    }

    private void updateMousePosition(final MouseEvent e) {
        mousePosition[RenderConstants.X] = e.getX();
        mousePosition[RenderConstants.Y] = e.getY();
    }

    public int[] getMousePosition() {
        return mousePosition;
    }

    public boolean isPressed(final JBJGLKey key) {
        return characterPressedStatusMap.getOrDefault(key, false);
    }

    private void handleKeyEvent(final JBJGLKeyEvent event) {
        // check in tasks
        for (JBJGLInputTask task : tasks)
            if (task.getEvent().equals(event)) {
                task.execute();
                return;
            }

        // add to list of unprocessed events iff there is no matching task
        eventList.add(event);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        JBJGLKey key = JBJGLKey.fromKeyEvent(e);

        handleKeyEvent(
                JBJGLKeyEvent.generate(key, JBJGLKeyEvent.Action.TYPE)
        );
    }

    @Override
    public void keyPressed(KeyEvent e) {
        JBJGLKey key = JBJGLKey.fromKeyEvent(e);

        if (characterPressedStatusMap.containsKey(key) && characterPressedStatusMap.get(key))
            return;

        characterPressedStatusMap.put(key, true);
        handleKeyEvent(
                JBJGLKeyEvent.generate(key, JBJGLKeyEvent.Action.PRESS)
        );
    }

    @Override
    public void keyReleased(KeyEvent e) {
        JBJGLKey key = JBJGLKey.fromKeyEvent(e);

        if (characterPressedStatusMap.containsKey(key) && !characterPressedStatusMap.get(key))
            return;

        characterPressedStatusMap.put(key, false);
        handleKeyEvent(
                JBJGLKeyEvent.generate(key, JBJGLKeyEvent.Action.RELEASE)
        );
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        eventList.add(
                JBJGLMouseEvent.generate(new int[] {
                        e.getX(), e.getY()
                }, JBJGLMouseEvent.Action.CLICK)
        );
    }

    @Override
    public void mousePressed(MouseEvent e) {
        eventList.add(
                JBJGLMouseEvent.generate(new int[] {
                        e.getX(), e.getY()
                }, JBJGLMouseEvent.Action.DOWN)
        );
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        eventList.add(
                JBJGLMouseEvent.generate(new int[] {
                        e.getX(), e.getY()
                }, JBJGLMouseEvent.Action.UP)
        );
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        updateMousePosition(e);
        eventList.add(
                JBJGLMoveEvent.generate(new int[] {
                        e.getX(), e.getY()
                }, JBJGLMoveEvent.Action.ENTER)
        );
    }

    @Override
    public void mouseExited(MouseEvent e) {
        updateMousePosition(e);
        eventList.add(
                JBJGLMoveEvent.generate(new int[] {
                        e.getX(), e.getY()
                }, JBJGLMoveEvent.Action.EXIT)
        );
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateMousePosition(e);
        eventList.add(
                JBJGLMoveEvent.generate(new int[] {
                        e.getX(), e.getY()
                }, JBJGLMoveEvent.Action.DRAG)
        );
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateMousePosition(e);
        eventList.add(
                JBJGLMoveEvent.generate(new int[] {
                        e.getX(), e.getY()
                }, JBJGLMoveEvent.Action.MOVE)
        );
    }

    @Override
    public void windowOpened(WindowEvent e) {
        eventList.add(
                JBJGLWindowEvent.generate(JBJGLWindowEvent.Action.OPENED)
        );
    }

    @Override
    public void windowClosing(WindowEvent e) {
        eventList.add(
                JBJGLWindowEvent.generate(JBJGLWindowEvent.Action.CLOSING)
        );
    }

    @Override
    public void windowClosed(WindowEvent e) {
        eventList.add(
                JBJGLWindowEvent.generate(JBJGLWindowEvent.Action.CLOSED)
        );
    }

    @Override
    public void windowIconified(WindowEvent e) {
        eventList.add(
                JBJGLWindowEvent.generate(JBJGLWindowEvent.Action.ICONIFIED)
        );
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        eventList.add(
                JBJGLWindowEvent.generate(JBJGLWindowEvent.Action.DEICONIFIED)
        );
    }

    @Override
    public void windowActivated(WindowEvent e) {
        eventList.add(
                JBJGLWindowEvent.generate(JBJGLWindowEvent.Action.ACTIVATED)
        );
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        eventList.add(
                JBJGLWindowEvent.generate(JBJGLWindowEvent.Action.DEACTIVATED)
        );
    }
}
