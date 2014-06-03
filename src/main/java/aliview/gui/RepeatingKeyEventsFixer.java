package aliview.gui;


import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Timer;

/**
 * This {@link AWTEventListener} tries to work around for KEY_PRESSED / KEY_TYPED/     KEY_RELEASED repeaters.
 * 
 * If you wish to obtain only one pressed / typed / released, no repeatings (i.e., when the button is hold for a long time).
 * Use new RepeatingKeyEventsFixer().install() as a first line in main() method.
 * 
 * Based on xxx
 * Which was done by Endre Stølsvik and inspired by xxx (hyperlinks stipped out due to stackoverflow policies)
 * 
 * Refined by Jakub Gemrot not only to fix KEY_RELEASED events but also KEY_PRESSED and KEY_TYPED repeatings. Tested under Win7.
 * 
 * If you wish to test the class, just uncomment all System.out.println(...)s.
 * 
 * @author Endre Stølsvik
 * @author Jakub Gemrot
 */
public class RepeatingKeyEventsFixer implements AWTEventListener {

 public static final int RELEASED_LAG_MILLIS = 1;

 private static boolean assertEDT() {
  if (!EventQueue.isDispatchThread()) {
   throw new AssertionError("Not EDT, but [" + Thread.currentThread() + "].");
  }
  return true;
 }

 private Map<Integer, ReleasedAction> _releasedMap = new HashMap<Integer, ReleasedAction>();
 private Set<Integer> _pressed = new HashSet<Integer>();
 private Set<Character> _typed = new HashSet<Character>();

 public void install() {
  Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
 }

 public void remove() {
  Toolkit.getDefaultToolkit().removeAWTEventListener(this);
 }

 public void eventDispatched(AWTEvent event) {
  assert event instanceof KeyEvent : "Shall only listen to KeyEvents, so no other events shall come here";
  assert assertEDT(); // REMEMBER THAT THIS IS SINGLE THREADED, so no need
       // for synch.

  // ?: Is this one of our synthetic RELEASED events?
  if (event instanceof Reposted) {
   //System.out.println("REPOSTED: " + ((KeyEvent)event).getKeyChar());
   // -> Yes, so we shalln't process it again.
   return;
  }

  final KeyEvent keyEvent = (KeyEvent) event;

  // ?: Is this already consumed?
  // (Note how events are passed on to all AWTEventListeners even though a
  // previous one consumed it)
  if (keyEvent.isConsumed()) {
   return;
  }

  // ?: KEY_TYPED event? (We're only interested in KEY_PRESSED and
  // KEY_RELEASED).
  if (event.getID() == KeyEvent.KEY_TYPED) {
   if (_typed.contains(keyEvent.getKeyChar())) {
    // we're being retyped -> prevent!
    //System.out.println("TYPED: " + keyEvent.getKeyChar() + " (CONSUMED)");
    keyEvent.consume();  
   } else {
    // -> Yes, TYPED, for a first time
    //System.out.println("TYPED: " + keyEvent.getKeyChar());
    _typed.add(keyEvent.getKeyChar());
   }
   return;
  } 

  // ?: Is this RELEASED? (the problem we're trying to fix!)
  if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
   // -> Yes, so stick in wait
   /*
    * Really just wait until "immediately", as the point is that the
    * subsequent PRESSED shall already have been posted on the event
    * queue, and shall thus be the direct next event no matter which
    * events are posted afterwards. The code with the ReleasedAction
    * handles if the Timer thread actually fires the action due to
    * lags, by cancelling the action itself upon the PRESSED.
    */
   final Timer timer = new Timer(RELEASED_LAG_MILLIS, null);
   ReleasedAction action = new ReleasedAction(keyEvent, timer);
   timer.addActionListener(action);
   timer.start();

   ReleasedAction oldAction = (ReleasedAction)_releasedMap.put(Integer.valueOf(keyEvent.getKeyCode()), action);
   if (oldAction != null) oldAction.cancel();

   // Consume the original
   keyEvent.consume();
   //System.out.println("RELEASED: " + keyEvent.getKeyChar() + " (CONSUMED)");
   return;
  }

  if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {

   if (_pressed.contains(keyEvent.getKeyCode())) {
    // we're still being pressed
    //System.out.println("PRESSED: " + keyEvent.getKeyChar() + " (CONSUMED)"); 
    keyEvent.consume();
   } else {   
    // Remember that this is single threaded (EDT), so we can't have
    // races.
    ReleasedAction action = (ReleasedAction) _releasedMap.get(keyEvent.getKeyCode());
    // ?: Do we have a corresponding RELEASED waiting?
    if (action != null) {
     // -> Yes, so dump it
     action.cancel();

    }
    _pressed.add(keyEvent.getKeyCode());
    //System.out.println("PRESSED: " + keyEvent.getKeyChar());    
   }

   return;
  }

  throw new AssertionError("All IDs should be covered.");
 }

 /**
  * The ActionListener that posts the RELEASED {@link RepostedKeyEvent} if
  * the {@link Timer} times out (and hence the repeat-action was over).
  */
 protected class ReleasedAction implements ActionListener {

  private final KeyEvent _originalKeyEvent;
  private Timer _timer;

  ReleasedAction(KeyEvent originalReleased, Timer timer) {
   _timer = timer;
   _originalKeyEvent = originalReleased;
  }

  void cancel() {
   assert assertEDT();
   _timer.stop();
   _timer = null;
   _releasedMap.remove(Integer.valueOf(_originalKeyEvent.getKeyCode()));   
  }

  
  public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
   assert assertEDT();
   // ?: Are we already cancelled?
   // (Judging by Timer and TimerQueue code, we can theoretically be
   // raced to be posted onto EDT by TimerQueue,
   // due to some lag, unfair scheduling)
   if (_timer == null) {
    // -> Yes, so don't post the new RELEASED event.
    return;
   }
   //System.out.println("REPOST RELEASE: " + _originalKeyEvent.getKeyChar());
   // Stop Timer and clean.
   cancel();
   // Creating new KeyEvent (we've consumed the original).
   KeyEvent newEvent = new RepostedKeyEvent(
     (Component) _originalKeyEvent.getSource(),
     _originalKeyEvent.getID(), _originalKeyEvent.getWhen(),
     _originalKeyEvent.getModifiers(), _originalKeyEvent
       .getKeyCode(), _originalKeyEvent.getKeyChar(),
     _originalKeyEvent.getKeyLocation());
   // Posting to EventQueue.
   _pressed.remove(_originalKeyEvent.getKeyCode());
   _typed.remove(_originalKeyEvent.getKeyChar());
   Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(newEvent);
  }
 }

 /**
  * Marker interface that denotes that the {@link KeyEvent} in question is
  * reposted from some {@link AWTEventListener}, including this. It denotes
  * that the event shall not be "hack processed" by this class again. (The
  * problem is that it is not possible to state
  * "inject this event from this point in the pipeline" - one have to inject
  * it to the event queue directly, thus it will come through this
  * {@link AWTEventListener} too.
  */
 public interface Reposted {
  // marker
 }

 /**
  * Dead simple extension of {@link KeyEvent} that implements
  * {@link Reposted}.
  */
 public static class RepostedKeyEvent extends KeyEvent implements Reposted {
  public RepostedKeyEvent(@SuppressWarnings("hiding") Component source,
    @SuppressWarnings("hiding") int id, long when, int modifiers,
    int keyCode, char keyChar, int keyLocation) {
   super(source, id, when, modifiers, keyCode, keyChar, keyLocation);
  }
 }

}