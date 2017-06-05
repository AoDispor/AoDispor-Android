package pt.aodispor.android.features.cardstack;

/**Akin to a pointer to a pointer. Allow to reference a cardstack even when changed without accessing the owner class*/
public class CardStackContainer {
    public CardStack cardStack;

    public CardStackContainer(){
        cardStack = new CardStack();
    }

}
