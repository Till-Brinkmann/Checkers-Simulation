package generic;
/**
 * <p>
 * Materialien zu den zentralen NRW-Abiturpr�fungen im Fach Informatik ab 2017
 * </p>
 * <p>
 * Generische Klasse Stack<ContentType>
 * </p>  
 * <p>
 * Objekte der generischen Klasse Stack (Keller, Stapel) verwalten beliebige
 * Objekte vom Typ ContentType nach dem Last-In-First-Out-Prinzip, d.h., das
 * zuletzt abgelegte Objekt wird als erstes wieder entnommen. Alle Methoden
 * haben eine konstante Laufzeit, unabh�ngig von der Anzahl der verwalteten
 * Objekte.
 * </p>
 * 
 * @author Qualit�ts- und Unterst�tzungsAgentur - Landesinstitut f�r Schule, Materialien zum schulinternen Lehrplan Informatik SII
 * @version Generisch_02 2014-02-21
 */
public class Stack<ContentType> {
  /* --------- Anfang der privaten inneren Klasse -------------- */

  private class StackNode {

    private ContentType content;
    private StackNode nextNode = null;

    /**
     * Ein neues Objekt vom Typ StackNode<ContentType> wird erschaffen. <br />
     * Der Inhalt wird per Parameter gesetzt. Der Verweis ist leer.
     * 
     * @param pContent der Inhalt des Knotens
     */
    public StackNode(ContentType pContent) {
      content = pContent;
    }

    /**
     * Der Verweis wird auf das Objekt, das als Parameter �bergeben wird,
     * gesetzt.
     * 
     * @param pNext der Nachfolger des Knotens
     */
    public void setNext(StackNode pNext) {
      nextNode = pNext;
    }

    /**
     * 
     * @return das Objekt, auf das der aktuelle Verweis zeigt
     */
    public StackNode getNext() {
      return nextNode;
    }

    /**
     * @return das Inhaltsobjekt vom Typ ContentType
     */
    public ContentType getContent() {
      return content;
    }
  }

  /* ----------- Ende der privaten inneren Klasse -------------- */

  private StackNode head; 
  
  /**
   * Ein leerer Stapel wird erzeugt. Objekte, die in diesem Stapel verwaltet
   * werden, m�ssen vom Typ ContentType sein.
   */
  public Stack() {
    head = null;
  }

  /**
   * Die Anfrage liefert den Wert true, wenn der Stapel keine Objekte
   * enth�lt, sonst liefert sie den Wert false.
   * 
   * @return true, falls der Stapel leer ist, sonst false
   */
  public boolean isEmpty() {
    return (head == null);
  }

  /**
   * Das Objekt pContentType wird oben auf den Stapel gelegt. Falls
   * pContentType gleich null ist, bleibt der Stapel unver�ndert.
   * 
   * @param pContent 
   *        das einzuf�gende Objekt vom Typ ContentType
   */
  public void push(ContentType pContent) {
    if (pContent != null) {
      StackNode node = new StackNode(pContent);
      node.setNext(head);
      head = node;
    }
  }

  /**
   * Das zuletzt eingef�gte Objekt wird von dem Stapel entfernt. Falls der
   * Stapel leer ist, bleibt er unver�ndert.
   */
  public void pop() {
    if (!isEmpty()) {
      head = head.getNext();
    }
  }

  /**
   * Die Anfrage liefert das oberste Stapelobjekt. Der Stapel bleibt
   * unver�ndert. Falls der Stapel leer ist, wird null zur�ckgegeben.
   * 
   * @return das oberste Stackelement vom Typ ContentType oder null, falls
   *         der Stack leer ist
   */
  public ContentType top() {
    if (!isEmpty()) {
      return head.getContent();
    } else {
      return null;
    }
  }
}

