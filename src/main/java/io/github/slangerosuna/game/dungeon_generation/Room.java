public abstract class Room {
    public Transform transform;
    public Collider collider;
    public Door[] doors;

    public void kill() {
        transform.kill();
        collider.kill();
    }

    public Room(Transform transform, Collider collider, Door... doors) {
        this.transform = transform;
        this.collider = collider;
        this.doors = doors;
    }

    public abstract void create(Scene scene);
}