package org.openjfx.GameController.InteractionManager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import org.openjfx.GameComponent.ComponentHitBoxCircle;
import org.openjfx.GameComponent.ComponentHitBoxRectangle;
import org.openjfx.GameComponent.Player;

public class InteractionHandler {
    public boolean handleInteraction(Pane gameRoot, Player player) {
        BooleanProperty isDead = new SimpleBooleanProperty(false);
        /*
         * + means it collides in its own loop
         *  - means it collides in other loops
         *
         * PlayerEquipment consists of
         * -> Bomb
         * -> EngineBlast
         * -> PlayerBullet
         * -> Melee
         * -> Rocket
         * -> OverCharge
         *
         *  Player will collide with
         * ++++ Enemy
         * ++++ EnemyBullet
         * ++++ Citizen
         * ++++ Collectible
         * ---- EnemySelfDestruct
         *
         * Enemy will collide with
         * ---- Player
         * ++++ PlayerEquipment
         * ++++ Citizen
         * ++++ Shield
         * ---- EnemySelfDestruct
         *
         * Citizen will collide with
         * ++++ PlayerEquipment
         * ++++ EnemyBullet
         * ++++ Enemy (Only Atlas)
         * ---- EnemySelfDestruct
         * ---- Player
         *
         * EnemySelfDestruct will collide with
         * ++++ Player
         * ++++ PlayerEquipment
         * ++++ Enemy
         * ++++ Citizen
         * ++++ Shield
         *
         * Shield will collide with
         * --- Enemy
         * +++ EnemyBullet
         * --- EnemySelfDestruct
         */
        gameRoot.getChildren().forEach(i -> { // first loop // for every shape in gameRoot
            ComponentHitBoxCircle circleTemp; // first temp circle hit box
            ComponentHitBoxRectangle rectangleTemp; // first temp rectangle hit box
            if (i instanceof ComponentHitBoxCircle) {
                //////////////////////
                //// FOR CIRCLES
                //////////////////////
                circleTemp = (ComponentHitBoxCircle) i; // cast first circle
                gameRoot.getChildren().forEach(j -> { // second loop
                    ComponentHitBoxCircle circleTemp2; // second temp circle hit box
                    ComponentHitBoxRectangle rectangleTemp2; // second temp rectangle hit box.
                    if (j instanceof ComponentHitBoxCircle) {
                        //////////////////////
                        //// FOR CIRCLES 2
                        //////////////////////
                        circleTemp2 = (ComponentHitBoxCircle) j; // cast second circle
                        if (circleTemp.getType().equals("player")) { // first one is player
                            if (circleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        isDead.set(true); // make return value true
                                        circleTemp.dead = true; // change circleTemp to dead status.
                                        circleTemp2.dead = true; // change circleTemp2 to dead status
                                    }
                                }
                            } else if (circleTemp2.getType().equals("enemy")) { // second one is enemy
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        isDead.set(true); // make return value true
                                        circleTemp.dead = true; // change circleTemp to dead status.
                                        circleTemp2.dead = true; // change circleTemp2 to dead status
                                    }
                                }
                            } else if (circleTemp2.getType().equals("civilian")) { // second one is Citizen
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        //TODO ADD CITIZEN INTERACTION HERE
                                    }
                                }
                            } else if (circleTemp2.getType().equals("collectible")) { // second is Collectible
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead, kill it.
                                        //TODO ADD COLLECTIBLE INTERACTION HERE
                                    }
                                }
                            }
                        } else if (circleTemp.getType().equals("enemy")) { // first one is enemy
                            if (circleTemp2.getType().equals("playerEquipment")) { // second one is playerEquipment
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (circleTemp2.getSpecificType().equals("playerBullet")) { // if second one is player bullet
                                        circleTemp2.dead = true;
                                        circleTemp.dead = true;
                                    } else if (circleTemp2.getSpecificType().equals("bomb")) { // if second one is player bullet
                                        circleTemp.dead = true;
                                        circleTemp2.dead = true;
                                    } else if (circleTemp2.getSpecificType().equals("explosion")) {
                                        circleTemp.dead = true;
                                    } else if (circleTemp2.getSpecificType().equals("barrier")) {
                                        circleTemp.dead = true;
                                    }
                                    //TODO PlayerEquipment Interaction here
                                }
                            } else if (circleTemp2.getType().equals("citizen")) { // second one is Citizen
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO ADD CITIZEN INTERACTION HERE
                                }
                            } else if (circleTemp2.getType().equals("shield")) { // second one is shield
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO ADD Shield INTERACTION HERE
                                    circleTemp.dead = true;
                                    circleTemp2.dead = true;
                                }
                            }
                        } else if (circleTemp.getType().equals("citizen")) { // first one is citizen
                            if (circleTemp2.getType().equals("playerEquipment")) { // second one is playerEquipment
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (circleTemp2.getSpecificType().equals("playerBullet")) { // if second one is player bullet
                                        circleTemp2.dead = true;
                                        circleTemp.dead = true;
                                    }
                                    //TODO Citizen-PlayerEquipment Interaction here
                                }
                            } else if (circleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO ADD CITIZEN INTERACTION HERE
                                }
                            }
                        } else if (circleTemp.getType().equals("enemySelfDestruct")) { // first one is enemySelfDestruct
                            if (circleTemp2.getType().equals("player")) { // second one is player
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        isDead.set(true); // make return value true
                                        circleTemp2.dead = true; // change circleTemp2 to dead status
                                    }
                                }
                            } else if (circleTemp2.getType().equals("playerEquipment")) { // second one is playerEquipment
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (circleTemp2.getSpecificType().equals("playerBullet")) // if second one is player bullet
                                        circleTemp2.dead = true;
                                    //TODO ADD ENEMY SELF DESTRUCT - PLAYER EQUIPMENT INTERACTION HERE
                                }
                            } else if (circleTemp2.getType().equals("enemy")) { // second one is playerEquipment
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    circleTemp2.dead = true; // change circleTemp2 to dead status
                                }
                            } else if (circleTemp2.getType().equals("citizen")) { // second one is playerEquipment
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    circleTemp2.dead = true; // change circleTemp2 to dead status
                                }
                            } else if (circleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    circleTemp2.dead = true; // change circleTemp2 to dead status
                                }
                            } else if (circleTemp2.getType().equals("shield")) { // second one is enemyBullet
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO IF ITS OVERCHARGED
                                    circleTemp2.dead = true; // change circleTemp2 to dead status
                                    circleTemp.dead = true;
                                }
                            }
                        } else if (circleTemp.getType().equals("shield")) { // first one is shield
                            if (circleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO IF ITS OVERCHARGED
                                    circleTemp.dead = true;
                                    circleTemp2.dead = true; // change circleTemp2 to dead status
                                }
                            }
                        } else if (circleTemp.getType().equals("playerEquipment")) { // first one is citizen
                            if (circleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (circleTemp.getSpecificType().equals("barrier")) {
                                    if (circleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                        circleTemp2.dead = true; // change circleTemp2 to dead status
                                    }
                                }
                            }
                        }
                    } else if (j instanceof ComponentHitBoxRectangle) { // for rectangles
                        //////////////////////
                        //// FOR RECTANGLES 2
                        //////////////////////
                        rectangleTemp2 = (ComponentHitBoxRectangle) j; // cast second rectangle
                        if (circleTemp.getType().equals("player")) { // first one is player
                            if (rectangleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        isDead.set(true); // make return value true
                                        circleTemp.dead = true; // change circleTemp to dead status.
                                        rectangleTemp2.dead = true; // change rectangleTemp2 to dead status
                                    }
                                }
                            } else if (rectangleTemp2.getType().equals("enemy")) { // second one is enemy
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        isDead.set(true); // make return value true
                                        circleTemp.dead = true; // change circleTemp to dead status.
                                        rectangleTemp2.dead = true; // change rectangleTemp2 to dead status
                                    }
                                }
                            } else if (rectangleTemp2.getType().equals("citizen")) { // second one is Citizen
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect

                                }
                            } else if (rectangleTemp2.getType().equals("collectible")) { // second is Collectible
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    String abilityType = rectangleTemp2.getSpecificType();
                                    if (player.addAbility(abilityType)) {
                                        rectangleTemp2.dead = true;
                                    }
                                }
                            }
                        } else if (circleTemp.getType().equals("enemy")) { // first one is enemy
                            if (rectangleTemp2.getType().equals("playerEquipment")) { // second one is playerEquipment
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    if (rectangleTemp2.getSpecificType().equals("playerBullet")) { // if second one is player bullet
                                        rectangleTemp2.dead = true;
                                        circleTemp.dead = true;
                                    } else if (rectangleTemp2.getSpecificType().equals("engineBlast")) {
                                        circleTemp.dead = true;
                                    } else if (rectangleTemp2.getSpecificType().equals("melee")) {
                                        circleTemp.dead = true;
                                    } else if (rectangleTemp2.getSpecificType().equals("guidedRocket")) { //if second one is guided rocket
                                        circleTemp.dead = true;
                                    }
                                }
                            } else if (rectangleTemp2.getType().equals("citizen")) { // second one is Citizen
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO ADD CITIZEN INTERACTION HERE
                                }
                            }
                        } else if (circleTemp.getType().equals("citizen")) { // first one is citizen
                            if (rectangleTemp2.getType().equals("playerEquipment")) { // second one is playerEquipment
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    if (rectangleTemp2.getSpecificType().equals("playerBullet")) { // if second one is player bullet
                                        rectangleTemp2.dead = true;
                                        circleTemp.dead = true;
                                    }
                                    //TODO Citizen-PlayerEquipment Interaction here
                                }
                            } else if (rectangleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    circleTemp.dead = true;
                                    rectangleTemp2.dead = true;
                                    //TODO ADD CITIZEN INTERACTION HERE
                                }
                            }
                        } else if (circleTemp.getType().equals("enemySelfDestruct")) { // first one is enemySelfDestruct
                            if (rectangleTemp2.getType().equals("player")) { // second one is player
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        isDead.set(true); // make return value true
                                        rectangleTemp2.dead = true; // change rectangleTemp2 to dead status
                                    }
                                }
                            } else if (rectangleTemp2.getType().equals("playerEquipment")) { // second one is playerEquipment
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    if (rectangleTemp2.getSpecificType().equals("playerBullet")) // if second one is player bullet
                                        rectangleTemp2.dead = true;
                                    //TODO ADD ENEMY SELF DESTRUCT - PLAYER EQUIPMENT INTERACTION HERE
                                }
                            } else if (rectangleTemp2.getType().equals("enemy")) { // second one is playerEquipment
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    rectangleTemp2.dead = true; // change rectangleTemp2 to dead status
                                }
                            } else if (rectangleTemp2.getType().equals("citizen")) { // second one is playerEquipment
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    rectangleTemp2.dead = true; // change rectangleTemp2 to dead status
                                }
                            } else if (rectangleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    rectangleTemp2.dead = true; // change rectangleTemp2 to dead status
                                }
                            }
                        } else if (circleTemp.getType().equals("shield")) { // first one is shield
                            if (rectangleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (circleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    circleTemp.dead = true;
                                    rectangleTemp2.dead = true; // change rectangleTemp2 to dead status
                                }
                            }
                        }
                    }
                });
            } else if (i instanceof ComponentHitBoxRectangle) {
                //////////////////////
                //// FOR RECTANGLES
                //////////////////////
                rectangleTemp = (ComponentHitBoxRectangle) i; // cast first rectangle
                gameRoot.getChildren().forEach(j -> { // second loop
                    ComponentHitBoxCircle circleTemp2; // second temp circle hit box
                    ComponentHitBoxRectangle rectangleTemp2; // second temp rectangle hit box.
                    if (j instanceof ComponentHitBoxCircle) {
                        //////////////////////
                        //// FOR CIRCLES 2
                        //////////////////////
                        circleTemp2 = (ComponentHitBoxCircle) j; // cast second circle
                        if (rectangleTemp.getType().equals("player")) { // first one is player
                            if (circleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (rectangleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        isDead.set(true); // make return value true
                                        rectangleTemp.dead = true; // change rectangleTemp to dead status.
                                        circleTemp2.dead = true; // change circleTemp2 to dead status
                                    }
                                }
                            } else if (circleTemp2.getType().equals("enemy")) { // second one is enemy
                                if (rectangleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        isDead.set(true); // make return value true
                                        rectangleTemp.dead = true; // change rectangle to dead status.
                                        circleTemp2.dead = true; // change circleTemp2 to dead status
                                    }
                                }
                            } else if (circleTemp2.getType().equals("citizen")) { // second one is Citizen
                                if (rectangleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        //TODO ADD CITIZEN INTERACTION HERE
                                    }
                                }
                            } else if (circleTemp2.getType().equals("collectible")) { // second is Collectible
                                if (rectangleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    String abilityType = circleTemp2.getSpecificType();
                                    if (player.addAbility(abilityType)) {
                                        circleTemp2.dead = true;
                                    }
                                }
                            }
                        } else if (rectangleTemp.getType().equals("enemy")) { // first one is enemy
                            if (circleTemp2.getType().equals("playerEquipment")) { // second one is playerEquipment
                                if (rectangleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    if (circleTemp2.getSpecificType().equals("playerBullet")) { // if second one is player bullet
                                        circleTemp2.dead = true;
                                        rectangleTemp.dead = true;
                                    } else if (circleTemp2.getSpecificType().equals("bomb")) { // if second one is player bullet
                                        rectangleTemp.dead = true;
                                        circleTemp2.dead = true;
                                    } else if (circleTemp2.getSpecificType().equals("explosion")) {
                                        rectangleTemp.dead = true;
                                    } else if (circleTemp2.getSpecificType().equals("barrier")){
                                        rectangleTemp.dead = true;
                                    }
                                    //TODO PlayerEquipment Interaction here
                                }
                            } else if (circleTemp2.getType().equals("citizen")) { // second one is Citizen
                                if (rectangleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO ADD CITIZEN INTERACTION HERE
                                }
                            } else if (circleTemp2.getType().equals("shield")) { // second one is shield
                                if (rectangleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    rectangleTemp.dead = true;
                                    circleTemp2.dead = true;
                                    //TODO ADD Shield INTERACTION HERE
                                }
                            }
                        } else if (rectangleTemp.getType().equals("citizen")) { // first one is citizen
                            if (circleTemp2.getType().equals("playerEquipment")) { // second one is playerEquipment
                                if (rectangleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO Citizen-PlayerEquipment Interaction here
                                    if (circleTemp2.getSpecificType().equals("playerBullet")) { // if second one is player bullet
                                        circleTemp2.dead = true;
                                        rectangleTemp.dead = true;
                                    }
                                }
                            } else if (circleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (rectangleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO ADD CITIZEN INTERACTION HERE
                                    rectangleTemp.dead = true; // change rectangle to dead status.
                                    circleTemp2.dead = true; // change circleTemp2 to dead status
                                }
                            }
                        } else if (rectangleTemp.getType().equals("playerEquipment")) { // first one is playerEquipment
                            if (circleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (rectangleTemp.getSpecificType().equals("playerBullet")) {
                                    if (rectangleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                        rectangleTemp.dead = true; // change rectangle to dead status.
                                        circleTemp2.dead = true; // change circleTemp2 to dead status
                                    }
                                } else if (rectangleTemp.getSpecificType().equals("melee")) {
                                    if (rectangleTemp.getBoundsInParent().intersects(circleTemp2.getBoundsInParent())) { // if they intersect
                                        circleTemp2.dead = true; // change circleTemp2 to dead status
                                    }
                                }
                            }
                        }
                    } else if (j instanceof ComponentHitBoxRectangle) { // for rectangles
                        //////////////////////
                        //// FOR RECTANGLES 2
                        //////////////////////
                        rectangleTemp2 = (ComponentHitBoxRectangle) j; // cast second rectangle
                        if (rectangleTemp.getType().equals("player")) { // first one is player
                            if (rectangleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (rectangleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        isDead.set(true); // make return value true
                                        rectangleTemp.dead = true; // change rectangleTemp to dead status.
                                        rectangleTemp2.dead = true; // change rectangleTemp2 to dead status
                                    }
                                }
                            } else if (rectangleTemp2.getType().equals("enemy")) { // second one is enemy
                                if (rectangleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        isDead.set(true); // make return value true
                                        rectangleTemp.dead = true; // change rectangle to dead status.
                                        rectangleTemp2.dead = true; // change rectangleTemp2 to dead status
                                    }
                                }
                            } else if (rectangleTemp2.getType().equals("civilian")) { // second one is Citizen
                                if (rectangleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    if (!isDead.get()) { // if the player is not dead
                                        rectangleTemp2.saved = true;
                                    }
                                }
                            } else if (rectangleTemp2.getType().equals("collectible")) { // second is Collectible
                                if (rectangleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    if (player.addAbility(rectangleTemp2.getSpecificType())) {
                                        rectangleTemp2.dead = true;
                                    }

                                }
                            }
                        } else if (rectangleTemp.getType().equals("enemy")) { // first one is enemy
                            if (rectangleTemp2.getType().equals("playerEquipment")) { // second one is playerEquipment
                                if (rectangleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    if (rectangleTemp2.getSpecificType().equals("playerBullet")) { // if second one is player bullet
                                        rectangleTemp2.dead = true;
                                        rectangleTemp.dead = true;
                                    } else if (rectangleTemp2.getSpecificType().equals("engineBlast")) { // if second one is engine blast
                                        rectangleTemp.dead = true;
                                    } else if (rectangleTemp2.getSpecificType().equals("melee")) {
                                        rectangleTemp.dead = true;
                                    } else if (rectangleTemp2.getSpecificType().equals("guidedRocket")) { //if second one is guided rocket
                                        rectangleTemp.dead = true;
                                    }
                                    //TODO PlayerEquipment Interaction here
                                }
                            } else if (rectangleTemp2.getType().equals("citizen")) { // second one is Citizen
                                if (rectangleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO ADD CITIZEN INTERACTION HERE
                                }
                            }
                        } else if (rectangleTemp.getType().equals("citizen")) { // first one is citizen
                            if (rectangleTemp2.getType().equals("playerEquipment")) { // second one is playerEquipment
                                if (rectangleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO Citizen-PlayerEquipment Interaction here
                                    if (rectangleTemp2.getSpecificType().equals("playerBullet")) { // if second one is player bullet
                                        rectangleTemp2.dead = true;
                                        rectangleTemp.dead = true;
                                    }
                                }
                            } else if (rectangleTemp2.getType().equals("enemyBullet")) { // second one is enemyBullet
                                if (rectangleTemp.getBoundsInParent().intersects(rectangleTemp2.getBoundsInParent())) { // if they intersect
                                    //TODO ADD CITIZEN INTERACTION HERE
                                    rectangleTemp.dead = true; // change rectangle to dead status.
                                    rectangleTemp2.dead = true; // change rectangleTemp to dead status
                                }
                            }
                        }
                    }
                });
            }
        });
        return isDead.get();
    }
}
