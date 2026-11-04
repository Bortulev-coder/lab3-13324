import lab3.*;
class GigaChatCheckedProbe {
    void exact(RumBottle bottle, Person owner) { bottle.consume(owner, 500); }
    void less(RumBottle bottle, Person owner) { bottle.consume(owner, 200); }
    void twice(RumBottle bottle, Person owner) {
        bottle.consume(owner, 200);
        bottle.consume(owner, 150);
    }
}
