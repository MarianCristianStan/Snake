# 🐍 Snake Game

A modern and enhanced version of the classic **Snake**, focused on improving player–game interaction and overall gameplay fluidity.  
The game introduces **free directional movement**, **dynamic border behavior**, **multiple fruit types**, and **impactful power‑ups** that significantly change how each play session unfolds.  
It supports both **keyboard** and **controller**, featuring automatic input switching to ensure seamless control at all times.

## Key Features
- Free movement based on smooth head rotation  
- Dynamic borders with three states: Idle (green), Warning (yellow), Danger (red)  
- Two fruit types: Apple (+1), Pineapple (+2)  
- Four power-ups: Speed, Growth, Magnet, Shield  
- Automatic keyboard ↔ controller input switching  
- Quality-of-life handling for controller disconnect/connect  
- Best Scores tracking and menu navigation support  

## Technical Highlights
- Custom game loop with separate FPS and UPS handling  
- Modular architecture (Entities, GameStates, Inputs, Utils)  
- Controller support via JNA and SDL  
- Consistent update/draw cycle for each game state  

---

## 🎥 Gameplay Demo  
👉 [Watch the demo video](video/Snake_Game_Stan_Neagoe.mp4)
