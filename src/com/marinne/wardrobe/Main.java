package com.marinne.wardrobe;

import java.nio.file.Path;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Path workspace = Path.of(System.getProperty("user.dir"));
        WardrobeAppFrame.launch(workspace);
    }
}
