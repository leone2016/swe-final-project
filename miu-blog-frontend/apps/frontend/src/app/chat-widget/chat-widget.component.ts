import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-chat-widget',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './chat-widget.component.html',
  styleUrls: ['./chat-widget.component.css'],
})
export class ChatWidgetComponent {
  @ViewChild('messagesContainer') messagesContainer!: ElementRef;
  isOpen = false;
  message = '';
  messages: { role: string; content: string }[] = [];
  loading = false;

  constructor(private http: HttpClient) {}

  toggleChat() {
    this.isOpen = !this.isOpen;
    setTimeout(() => this.scrollToBottom(), 100);
  }

  sendMessage() {
    if (!this.message.trim()) return;

    const userMsg = { role: 'user', content: this.message };
    this.messages.push(userMsg);
    this.message = '';
    this.loading = true;
    this.scrollToBottom();

    this.http
      .post<any>('http://localhost:8092/api/chat', {
        model: 'llama3.2:latest',
        messages: [userMsg],
      })
      .subscribe({
        next: (res) => {
          this.loading = false;
          const text = res?.reply ?? JSON.stringify(res);
          this.messages.push({ role: 'assistant', content: text });
          this.scrollToBottom();
        },
        error: () => {
          this.loading = false;
          this.messages.push({
            role: 'assistant',
            content: 'Error contacting Edd! Try again later.',
          });
          this.scrollToBottom();
        },
      });
  }

  private scrollToBottom() {
    if (this.messagesContainer) {
      setTimeout(() => {
        this.messagesContainer.nativeElement.scrollTop = this.messagesContainer.nativeElement.scrollHeight;
      }, 100);
    }
  }
}
