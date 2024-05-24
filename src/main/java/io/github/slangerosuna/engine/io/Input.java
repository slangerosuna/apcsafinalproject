package io.github.slangerosuna.engine.io;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import io.github.slangerosuna.engine.core.ecs.Resource;

public class Input implements Resource {
    public static int type = Resource.registerResource("Input");
    public int getType() { return type; }
    public void kill() { destroy(); }

	private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
	private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
	private static boolean focused = false;
	public static boolean isFocused() { return focused; }

	private static double mouseX, mouseY;
	private static double scrollX, scrollY;

	private GLFWKeyCallback keyboard;
	private GLFWCursorPosCallback mouseMove;
	private GLFWMouseButtonCallback mouseButtons;
	private GLFWScrollCallback mouseScroll;

	public Input() {
		keyboard = new GLFWKeyCallback() {
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key < 0 || key >= keys.length)
					return;
				keys[key] = (action != GLFW.GLFW_RELEASE);
				if (keys[GLFW.GLFW_KEY_ESCAPE]) {
					GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
					focused = false;
				}
			}
		};

		mouseMove = new GLFWCursorPosCallback() {
			public void invoke(long window, double xpos, double ypos) {
				mouseX = xpos;
				mouseY = ypos;
			}
		};

		mouseButtons = new GLFWMouseButtonCallback() {
			public void invoke(long window, int button, int action, int mods) {
				buttons[button] = (action != GLFW.GLFW_RELEASE);
				GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
				focused = true;
			}
		};

		mouseScroll = new GLFWScrollCallback() {
			public void invoke(long window, double offsetx, double offsety) {
				scrollX += offsetx;
				scrollY += offsety;
			}
		};
	}

	public static boolean isKeyDown(int key) { return keys[key]; }
	public static boolean isButtonDown(int button) { return buttons[button]; }

	public void destroy() {
		keyboard.free();
		mouseMove.free();
		mouseButtons.free();
		mouseScroll.free();
	}

	public static double getMouseX() { return mouseX; }
	public static double getMouseY() { return mouseY; }
	public static double getScrollX() { return scrollX; }
	public static double getScrollY() { return scrollY; }
	public GLFWKeyCallback getKeyboardCallback() { return keyboard; }
	public GLFWCursorPosCallback getMouseMoveCallback() { return mouseMove; }
	public GLFWMouseButtonCallback getMouseButtonsCallback() { return mouseButtons; }
	public GLFWScrollCallback getMouseScrollCallback() { return mouseScroll; }
}
