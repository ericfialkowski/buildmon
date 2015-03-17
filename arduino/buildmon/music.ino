#include <avr/pgmspace.h>
#include "pitches.h"

PROGMEM const uint16_t buzz_melody[] = { NOTE_G1, NOTE_G1, NOTE_G1, NOTE_DS1, NOTE_AS1, NOTE_G1, NOTE_DS1, NOTE_AS1, NOTE_G1 };
PROGMEM const uint16_t buzz_note_durations[] = { 2, 2, 2, 4, 4, 2, 4, 4, 2 };
int buzz_notes_to_play = 9;

PROGMEM const uint16_t beep_melody[] = { NOTE_E7, NOTE_E7, 0, NOTE_E7,   0, NOTE_C7, NOTE_E7, 0,  NOTE_G7, 0, 0,  0,  NOTE_G6, 0, 0, 0 };
PROGMEM const uint16_t beep_note_durations[] = { 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12 };
int beep_notes_to_play = 16;

int notes_to_play = 0;
int current_note = 0;

int melody[16];
int note_durations[16];


void play_note()
{
  if ( current_note < notes_to_play)
  {
    // to calculate the note duration, take one second
    // divided by the note type.
    //e.g. quarter note = 1000 / 4, eighth note = 1000/8, etc.
    int noteDuration = 1000 / note_durations[current_note];
    tone(buzzer, melody[current_note], noteDuration);

    // to distinguish the notes, set a minimum time between them.
    // the note's duration + 30% seems to work well:
    int pauseBetweenNotes = noteDuration * 1.30;
    delay(pauseBetweenNotes);
    // stop the tone playing:
    noTone(buzzer);
    current_note++;
  }
  else
  {
    notes_to_play = 0;
    current_note = 0;
    delay(100);
  }
}

void buzz()
{
  int len = sizeof(int) * buzz_notes_to_play;
  memcpy_P(melody, buzz_melody, len);
  memcpy_P(note_durations, buzz_note_durations, len);

  notes_to_play = buzz_notes_to_play;
  current_note = 0;
}

void beep()
{
  int len = sizeof(int) * beep_notes_to_play;
  memcpy_P(melody, beep_melody, len);
  memcpy_P(note_durations, beep_note_durations, len);

  notes_to_play = beep_notes_to_play;
  current_note = 0;
}

