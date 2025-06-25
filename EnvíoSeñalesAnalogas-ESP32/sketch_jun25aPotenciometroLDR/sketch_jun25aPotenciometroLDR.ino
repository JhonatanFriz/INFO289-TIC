#define LDR_PIN      34  // GPIO34 - Entrada analógica desde LDR
#define POT_PIN      32  // GPIO32 - Entrada analógica desde potenciómetro

void setup() {
  Serial.begin(115200);
}

void loop() {
  int ldrValue = analogRead(LDR_PIN);
  int potValue = analogRead(POT_PIN);

  Serial.print("LDR: ");
  Serial.print(ldrValue);
  Serial.print("  |  Potenciómetro: ");
  Serial.println(potValue);

  delay(500);
}