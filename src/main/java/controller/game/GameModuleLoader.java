package controller.game;

import model.Model;
import nl.hanze.t23i.gamemodule.extern.AbstractGameModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class GameModuleLoader {
	private static final Logger logger = LogManager.getLogger(GameModuleLoader.class);
	private HashMap<String, Class<? extends AbstractGameModule>> gameModuleMap;
    private ArrayList<String> gameTypeList;
    private Model model;

    public GameModuleLoader(File modulePath, Model model) {
        this.model = model;
	    gameModuleMap =  new HashMap<>();
	    loadJarFiles(modulePath);

	    gameTypeList = new ArrayList<>(gameModuleMap.keySet());
    }

    public ArrayList<String> getGameTypeList() {
        return gameTypeList;
    }

    public AbstractGameModule loadGameModule(String gameTypeName, String playerOne, String playerTwo) {
        AbstractGameModule gameModule = null;

        Class<? extends AbstractGameModule> gameModuleClass = gameModuleMap.get(gameTypeName);

	    if (gameModuleClass == null) {
		    return null;
        }

        try {
            Constructor<? extends AbstractGameModule> constructor = gameModuleClass.getConstructor(String.class, String.class);
            gameModule = constructor.newInstance(playerOne, playerTwo);
        } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
	        logger.error("Error loading game module '{}': {}", gameTypeName, e);
        }

        return gameModule;
    }

    private void loadJarFiles(File modulePath) {
	    ArrayList<File> jarFiles = getJarFiles(modulePath);
	    logger.trace("Loading {} files. {}", jarFiles.size(), jarFiles);
	    for (File jarFile : jarFiles) {
		    try {
                ArrayList<Class<? extends AbstractGameModule>> moduleClassList = loadGameModuleClasses(jarFile);
                loadGameModules(moduleClassList);
            } catch (IOException e) {
	            logger.error("Error loading Jar file '{}': {}", jarFile.getAbsolutePath(), e);
		    }
        }
	    logger.trace("Loaded {} games. {}", gameModuleMap.size(), gameModuleMap.keySet());
    }

    private void loadGameModules(ArrayList<Class<? extends AbstractGameModule>> moduleClassList) {
	    for (Class<? extends AbstractGameModule> gameModuleClass : moduleClassList) {
		    try {
                String gameType = (String) gameModuleClass.getField("GAME_TYPE").get(null);
                try {
                    String[] pieces = (String[]) gameModuleClass.getField("GAME_PIECES").get(null);
                    model.putGameModulePieces(gameType, pieces);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                if (gameType == null || (gameType.trim().equals(""))) {
		            continue;
                }

                gameModuleMap.put(gameType, gameModuleClass);
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
	            logger.error("Found abstract module '{}' without GAME_TYPE. {}", gameModuleClass.getName(), e);
		    }
        }
    }

    /**
     * Loads game module classes from a Jar file.
     * <p>
     * This method will iterate the Jar file, looking for classes.<br />
     * When it finds a class it will check if it is a (subclass of) <code>AbstractGameModule</code>, it is not abstract and is public.<br />
     * If those criteria are met, then the class is added to the list of game module classes to be returned.
     *
     * @param file The Jar file containing the classes
     *
     * @return List of game module classes found in this Jar file
     *
     * @throws IOException If the Jar file is not found or the Jar file cannot be read
     */
    private ArrayList<Class<? extends AbstractGameModule>> loadGameModuleClasses(File file) throws IOException {
        // Create classloader for loader classes from within Jar file
        URLClassLoader jarClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, ClassLoader.getSystemClassLoader());
        JarFile jarFile = new JarFile(file);

        // Store found game module classes
        ArrayList<Class<? extends AbstractGameModule>> classList = new ArrayList<Class<? extends AbstractGameModule>>();

        // Find classes, iterate through Jar file looking for classes who's superclass is castable to AbstractGameModule and is not abstract.
	    for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
		    JarEntry jarEntry = entries.nextElement();

            // If entry is not a class: skip
		    if (!jarEntry.getName().endsWith(".class")) {
			    continue;
            }

            // Get class name: replace '/' with '.' and remove ".class" suffix
            String className = jarEntry.getName().replace('/', '.').replace(".class", "");

            try {
                // Load the class from the Jar file
                Class<?> clazz = Class.forName(className, true, jarClassLoader);

                try {
                    // Try to cast the class to an AbstractGameModule
                    Class<? extends AbstractGameModule> gameModuleClass = clazz.asSubclass(AbstractGameModule.class);

                    // If class is abstract: skip
	                if ((gameModuleClass.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
		                continue;
                    }

                    // If class not is public: skip
	                if ((gameModuleClass.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
		                continue;
                    }

                    // Add class to game module class list
                    classList.add(gameModuleClass);
                } catch (ClassCastException ignored) {
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        jarFile.close();

        return classList;
    }

    private ArrayList<File> getJarFiles(File modulePath) {
	    ArrayList<File> jarList = new ArrayList<>();

        if (modulePath == null || !modulePath.exists()) {
            return new ArrayList<>();
        }

	    for (File file : modulePath.listFiles()) {
		    String filename = file.getAbsolutePath();
		    if (filename.substring(filename.length() - 4).equalsIgnoreCase(".jar")) {
			    jarList.add(file);
            }
        }

        return jarList;
    }
}
