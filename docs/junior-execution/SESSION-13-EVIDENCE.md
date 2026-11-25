# Session 13 attachment verification

Phase 07 stacks on the unmerged Phase 06 head `b0ad327368e4c2276d81e85cb9310eddc2ec4de7`. Baseline release and JudgeIt gates remain open. All evidence uses synthetic data. Builder Lead reviewed each checkpoint.


## JR-121

**DONE.** Expected a current source-and-test map. At `b0ad327368e4c2276d81e85cb9310eddc2ec4de7`, `AttachmentController.upload` binds authenticated multipart `file`; `AttachmentService.upload` checks engagement membership, leader or log author, and DRAFT state before Tika byte detection and a PNG/JPEG/GIF/WebP allowlist. `application.yml` sets servlet caps. Existing `AttachmentIntegrationTest` covers PNG, plain text, disguised content, membership, state and CSRF. Its PNG helper contains only a header, and current MockMvc requests do not exercise the servlet multipart size limits. Historical design documentation is reference material. No application or test code changed.
